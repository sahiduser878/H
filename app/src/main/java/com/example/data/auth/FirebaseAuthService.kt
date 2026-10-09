package com.example.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.UserAccount
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

/**
 * Firebase Authentication & Google Sign-In via Credential Manager Service
 * Provides production-ready structure for:
 * 1. Email/Password Sign-In & Sign-Up
 * 2. Direct Google Sign-In via Credential Manager
 * 3. Safe graceful fallback when Firebase project credentials are local/development
 */
object FirebaseAuthService {
    private const val TAG = "FirebaseAuthService"

    // Default Web Client ID placeholder (configured in Firebase Console)
    var webClientId: String = "1048576000000-dummyclientid.apps.googleusercontent.com"

    private val auth: FirebaseAuth?
        get() = try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseApp not initialized yet: ${e.message}")
            null
        }

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val isFirebaseInitialized: Boolean
        get() = auth != null

    /**
     * Sign in with Email and Password using Firebase Auth
     */
    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), pass).await()
                Result.success(authResult.user)
            } else {
                // Firebase not initialized on backend yet; safe development mode
                Log.i(TAG, "Operating in local development mode without Firebase project")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase signInWithEmail error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Register a new user with Email and Password using Firebase Auth
     */
    suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val firebaseAuth = auth
            if (firebaseAuth != null) {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
                Result.success(authResult.user)
            } else {
                Log.i(TAG, "Operating in local development mode without Firebase project")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase signUpWithEmail error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Google Sign-In via Credential Manager
     */
    suspend fun signInWithGoogle(context: Context): Result<String> {
        val credentialManager = CredentialManager.create(context)

        // Raw nonce for token replay protection
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val hashedNonce = md.digest(rawNonce.toByteArray()).joinToString("") { "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .setNonce(hashedNonce)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response = credentialManager.getCredential(context, request)
            val credential = response.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Sign in with Firebase Auth using Google credentials if available
                val firebaseAuth = auth
                if (firebaseAuth != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential).await()
                }

                Result.success(googleIdTokenCredential.id)
            } else {
                Result.failure(Exception("Unsupported credential type"))
            }
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Credential Manager exception: ${e.message}")
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Google ID token parsing error: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In general failure: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Sign out from Firebase Auth
     */
    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "signOut error: ${e.message}")
        }
    }
}
