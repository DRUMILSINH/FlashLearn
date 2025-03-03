package com.rana.flashlearn

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    suspend fun signInWithEmailPassword(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(auth.currentUser?.uid ?: "Unknown User")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserEmail(): String? = auth.currentUser?.email
}
