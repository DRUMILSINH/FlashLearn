package com.rana.flashlearn

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class FlashLearnApplication : Application() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate() {
        super.onCreate()
        // ✅ Initialize Shared Preferences Manager
        sharedPrefManager = SharedPrefManager.getInstance(this)
    }

    /**
     * Determines the initial activity to launch based on user's login status
     * and whether it's the first app launch.
     */
    fun getInitialActivity(): Class<*> {
        return when {
            FirebaseAuth.getInstance().currentUser != null && sharedPrefManager.isLoggedIn() -> {
                MainActivity::class.java
            }
            else -> LoginActivity::class.java
        }
    }

    /**
     * Checks if the user is currently logged in.
     * This combines both Firebase authentication and shared preferences.
     */
    private fun isUserLoggedIn(): Boolean {
        val isLoggedInPref = sharedPrefManager.isLoggedIn()
        val isLoggedInFirebase = FirebaseAuth.getInstance().currentUser != null
        return isLoggedInPref && isLoggedInFirebase
    }
}
