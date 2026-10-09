# TAP GAME — TAP & WIN | Full-Stack Setup Guide

## 1. Project Overview
TAP GAME is a real-time 1v1 PvP speed tapping tournament platform built with Jetpack Compose (Kotlin) frontend and authoritative server-side ledger.

### Key Capabilities
- **Real-Time Matchmaking:** Queue, countdown, live duel with anti-cheat CPS rate limiting.
- **Double-Entry Wallet Ledger:** Immutable transactions, escrow pending balance for withdrawals, UPI deposit verifications.
- **Role-Based Admin Console:** Ban/restrict users, wallet adjustments with mandatory audit reason, financial approvals, maintenance controls.
- **Zero-Trust Security:** Strict Firestore rules preventing client balance tampering.

---

## 2. Firebase Cloud Deployment Steps

### Step 1: Provision Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/) and create a project named `tap-game-live`.
2. Enable **Firestore Database** in Production Mode (select cloud region `asia-south1` or closest to target audience).
3. Enable **Firebase Authentication** with Email/Password sign-in provider.

### Step 2: Deploy Firestore Security Rules
Deploy `firestore.rules` using the Firebase CLI:
```bash
firebase deploy --only firestore:rules
```

### Step 3: Required Firestore Indexes
Create composite indexes in Firestore for efficient queries:
```json
{
  "indexes": [
    {
      "collectionGroup": "matches",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "player1Id", "order": "ASCENDING" },
        { "fieldPath": "timestamp", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "transactions",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "userId", "order": "ASCENDING" },
        { "fieldPath": "timestamp", "order": "DESCENDING" }
      ]
    },
    {
      "collectionGroup": "users",
      "queryScope": "COLLECTION",
      "fields": [
        { "fieldPath": "matchesWon", "order": "DESCENDING" },
        { "fieldPath": "totalEarnings", "order": "DESCENDING" }
      ]
    }
  ]
}
```

### Step 4: Deploy Cloud Functions
Navigate to the `functions/` directory and deploy:
```bash
cd functions
npm install
firebase deploy --only functions
```

---

## 3. Environment Variable Template (`.env`)
```properties
# Cloud Backend Configuration
FIREBASE_PROJECT_ID=tap-game-live
UPI_MERCHANT_ID=tapgame.official@okhdfcbank
API_GATEWAY_WEBHOOK_SECRET=your_gateway_webhook_secret_here
```
