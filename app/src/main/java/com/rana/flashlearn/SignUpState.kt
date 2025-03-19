package com.rana.flashlearn

sealed class SignUpState {
    object Loading : SignUpState()
    data class Success(val message: String) : SignUpState()
    data class Error(val errorMessage: String) : SignUpState()
}
