package com.rana.flashlearn

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.rana.flashlearn.ui.MainActivity
import com.rana.flashlearn.ui.OnboardingActivity

class FlashLearnApplication : Application() {

    private val sharedPrefManager: SharedPrefManager by lazy {
        SharedPrefManager.getInstance(this)
    }

    fun getInitialActivity(): Class<*> {
        return when {
            isFirstLaunch() -> OnboardingActivity::class.java
            isUserLoggedIn() -> MainActivity::class.java
            else -> LoginActivity::class.java
        }
    }

    private fun isFirstLaunch(): Boolean {
        return sharedPrefManager.isFirstLaunch().also {
            if (it) sharedPrefManager.setFirstLaunch(false)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPrefManager.isLoggedIn() && FirebaseAuth.getInstance().currentUser != null
    }
}
