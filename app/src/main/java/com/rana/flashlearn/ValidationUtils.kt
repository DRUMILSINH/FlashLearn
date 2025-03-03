package com.rana.flashlearn

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isStrongPassword(password: String): Boolean {
        return password.length >= 6 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() } &&
                password.any { !it.isLetterOrDigit() }
    }
}