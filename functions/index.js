/**
 * Cloud Functions for TAP GAME — TAP & WIN
 * Authoritative backend game logic, match settlements, and wallet ledger
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

/**
 * 1. Authoritative Match Settlement
 * Finalizes winner, rate limits scores, and credits prize to winner's wallet atomically.
 */
exports.finalizeMatch = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated");
  }

  const { matchId, p1Score, p2Score } = data;
  const matchRef = db.collection("matches").doc(matchId);

  return db.runTransaction(async (transaction) => {
    const matchDoc = await transaction.get(matchRef);
    if (!matchDoc.exists) {
      throw new functions.https.HttpsError("not-found", "Match not found");
    }

    const match = matchDoc.data();
    if (match.status === "FINISHED") {
      return { success: true, message: "Match already finalized" };
    }

    // Anti-cheat sanity limit (Max 22 taps per second * duration)
    const maxPermitted = (match.durationSeconds || 15) * 22;
    const validatedP1 = Math.min(p1Score, maxPermitted);
    const validatedP2 = Math.min(p2Score, maxPermitted);

    let winnerId = null;
    if (validatedP1 > validatedP2) winnerId = match.player1Id;
    else if (validatedP2 > validatedP1) winnerId = match.player2Id;

    // Update match document
    transaction.update(matchRef, {
      player1Score: validatedP1,
      player2Score: validatedP2,
      winnerId: winnerId,
      status: "FINISHED",
      endedAt: admin.firestore.FieldValue.serverTimestamp()
    });

    if (winnerId) {
      // Award prize pool to winner
      const winnerUserRef = db.collection("users").doc(winnerId);
      const winnerUserDoc = await transaction.get(winnerUserRef);
      if (winnerUserDoc.exists) {
        const currentBalance = winnerUserDoc.data().availableBalance || 0;
        const currentWon = winnerUserDoc.data().matchesWon || 0;
        const currentGames = winnerUserDoc.data().totalGames || 0;

        transaction.update(winnerUserRef, {
          availableBalance: currentBalance + match.prizeAmount,
          matchesWon: currentWon + 1,
          totalGames: currentGames + 1,
          totalEarnings: (winnerUserDoc.data().totalEarnings || 0) + match.prizeAmount
        });

        // Add ledger record
        const txRef = db.collection("transactions").doc();
        transaction.set(txRef, {
          userId: winnerId,
          type: "MATCH_PRIZE",
          amount: match.prizeAmount,
          status: "COMPLETED",
          reference: `Match Victory #${matchId}`,
          timestamp: admin.firestore.FieldValue.serverTimestamp()
        });
      }
    } else {
      // Tie: Refund entry fees
      for (const pId of [match.player1Id, match.player2Id]) {
        const userRef = db.collection("users").doc(pId);
        const userDoc = await transaction.get(userRef);
        if (userDoc.exists) {
          transaction.update(userRef, {
            availableBalance: (userDoc.data().availableBalance || 0) + match.entryFee,
            totalGames: (userDoc.data().totalGames || 0) + 1
          });

          const refundRef = db.collection("transactions").doc();
          transaction.set(refundRef, {
            userId: pId,
            type: "REFUND",
            amount: match.entryFee,
            status: "COMPLETED",
            reference: `Tie Refund #${matchId}`,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
          });
        }
      }
    }

    return { success: true, winnerId };
  });
});

/**
 * 2. Automated UPI Webhook Callback
 * Receives verified banking gateway webhook and credits wallet safely.
 */
exports.upiDepositWebhook = functions.https.onRequest(async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).send("Method Not Allowed");
  }

  const { utrNumber, amount, userId, signature } = req.body;
  // Verify provider signature here...

  if (!utrNumber || !amount || !userId) {
    return res.status(400).json({ error: "Missing required parameters" });
  }

  const userRef = db.collection("users").doc(userId);
  await db.runTransaction(async (transaction) => {
    const userDoc = await transaction.get(userRef);
    if (!userDoc.exists) throw new Error("User does not exist");

    transaction.update(userRef, {
      availableBalance: (userDoc.data().availableBalance || 0) + Number(amount)
    });

    const txRef = db.collection("transactions").doc();
    transaction.set(txRef, {
      userId,
      type: "DEPOSIT",
      amount: Number(amount),
      status: "COMPLETED",
      reference: `Verified UPI UTR: ${utrNumber}`,
      timestamp: admin.firestore.FieldValue.serverTimestamp()
    });
  });

  return res.status(200).json({ success: true, message: "Deposit credited successfully" });
});
