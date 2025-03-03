package com.rana.flashlearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest

class SignUpViewModel(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) : ViewModel() {

    private val _signUpResult = MutableLiveData<SignUpState>()
    val signUpResult: LiveData<SignUpState> get() = _signUpResult

    fun registerUser(email: String, password: String, username: String) {
        if (!validateInputs(email, password, username)) return

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest { displayName = username }

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            _signUpResult.value = SignUpState.Success("User registered successfully")
                        } else {
                            _signUpResult.value = SignUpState.Error("Profile update failed")
                        }
                    }
                } else {
                    _signUpResult.value = SignUpState.Error(handleAuthError(task.exception))
                }
            }
    }

    private fun validateInputs(email: String, password: String, username: String): Boolean {
        return when {
            email.isEmpty() || !ValidationUtils.isValidEmail(email) -> {
                _signUpResult.value = SignUpState.Error("Invalid email")
                false
            }
            username.isEmpty() || username.length < 3 -> {
                _signUpResult.value = SignUpState.Error("Username must be at least 3 characters")
                false
            }
            password.isEmpty() || password.length < 6 || !ValidationUtils.isStrongPassword(password) -> {
                _signUpResult.value = SignUpState.Error("Weak password")
                false
            }
            else -> true
        }
    }

    private fun handleAuthError(exception: Exception?): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> "This email is already in use"
            is FirebaseAuthWeakPasswordException -> exception.reason ?: "Weak password"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
            is FirebaseAuthInvalidUserException -> "User does not exist"
            else -> exception?.message ?: "Sign-up failed"
        }
    }
}

// Sealed class for better state management
sealed class SignUpState {
    data class Success(val message: String) : SignUpState()
    data class Error(val errorMessage: String) : SignUpState()
}
