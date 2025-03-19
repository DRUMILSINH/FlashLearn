package com.rana.flashlearn

import android.util.Log
import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    // ✅ Sign-Up with Email & Password (Includes Username)
    suspend fun createUserWithEmailAndPassword(email: String, password: String, username: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            user?.let {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                it.updateProfile(profileUpdates).await()
            }

            Result.success(user?.uid ?: "Unknown User")
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("AuthRepository", "Email already in use", e)
            Result.failure(Exception("Email is already in use"))
        } catch (e: FirebaseAuthWeakPasswordException) {
            Log.e("AuthRepository", "Weak password", e)
            Result.failure(Exception("Weak password. Use at least 6 characters"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("AuthRepository", "Invalid email format", e)
            Result.failure(Exception("Invalid email format"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected sign-up error", e)
            Result.failure(e)
        }
    }

    // ✅ Sign-In with Email & Password
    suspend fun signInWithEmailPassword(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "Unknown User")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.e("AuthRepository", "Invalid credentials", e)
            Result.failure(Exception("Invalid email or password"))
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.e("AuthRepository", "User not found", e)
            Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected sign-in error", e)
            Result.failure(e)
        }
    }

    // ✅ Sign-In with Google
    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(auth.currentUser?.uid ?: "Unknown User")
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "Google sign-in error", e)
            Result.failure(Exception("Google sign-in failed"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected Google sign-in error", e)
            Result.failure(e)
        }
    }

    // ✅ Check if User is Logged In
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // ✅ Get Logged-In User's Email
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    // ✅ Get Logged-In User Object
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // ✅ Sign-Out Function
    fun signOut() {
        auth.signOut()
    }
}
