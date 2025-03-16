package com.rana.flashlearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _signUpResult = MutableLiveData<SignUpState>()
    val signUpResult: LiveData<SignUpState> get() = _signUpResult

    fun registerUser(email: String, password: String, confirmPassword: String, username: String) {
        if (!validateInputs(email, password, confirmPassword, username)) return

        _signUpResult.value = SignUpState.Loading

        viewModelScope.launch {
            authRepository.createUserWithEmailAndPassword(email, password)
                .fold(
                    onSuccess = {
                        val user = authRepository.getCurrentUser()
                        val profileUpdates = userProfileChangeRequest { displayName = username }

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                _signUpResult.value = SignUpState.Success("User registered successfully")
                            } else {
                                _signUpResult.value = SignUpState.Error("Profile update failed")
                            }
                        }
                    },
                    onFailure = { exception ->
                        _signUpResult.value = SignUpState.Error(handleAuthError(exception))
                    }
                )
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String, username: String): Boolean {
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
            password != confirmPassword -> {
                _signUpResult.value = SignUpState.Error("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun handleAuthError(exception: Throwable): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> "This email is already in use"
            is FirebaseAuthWeakPasswordException -> (exception as FirebaseAuthWeakPasswordException).reason ?: "Weak password"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
            is FirebaseAuthInvalidUserException -> "User does not exist"
            else -> exception.message ?: "Sign-up failed"
        }
    }
}

sealed class SignUpState {
    object Loading : SignUpState()
    data class Success(val message: String) : SignUpState()
    data class Error(val errorMessage: String) : SignUpState()
}