package com.rana.flashlearn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _signUpResult = MutableLiveData<SignUpState>()
    val signUpResult: LiveData<SignUpState> get() = _signUpResult

    fun registerUser(email: String, password: String, confirmPassword: String, username: String) {
        if (!validateInputs(email, password, confirmPassword, username)) return

        _signUpResult.value = SignUpState.Loading

        viewModelScope.launch {
            authRepository.createUserWithEmailAndPassword(email, password, username)
                .fold(
                    onSuccess = {
                        _signUpResult.value = SignUpState.Success("User registered successfully")
                    },
                    onFailure = { exception ->
                        _signUpResult.value = SignUpState.Error(exception.message ?: "Sign-up failed")
                    }
                )
        }
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String, username: String): Boolean {
        return when {
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _signUpResult.value = SignUpState.Error("Invalid email")
                false
            }
            username.isEmpty() || username.length < 3 -> {
                _signUpResult.value = SignUpState.Error("Username must be at least 3 characters")
                false
            }
            password.length < 6 -> {
                _signUpResult.value = SignUpState.Error("Password must be at least 6 characters")
                false
            }
            password != confirmPassword -> {
                _signUpResult.value = SignUpState.Error("Passwords do not match")
                false
            }
            else -> true
        }
    }
}
