package com.rana.flashlearn.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import com.rana.flashlearn.LoginActivity
import com.rana.flashlearn.R
import com.rana.flashlearn.SharedPrefManager

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPrefManager = SharedPrefManager.getInstance(this)

        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) { delay(2000) }

            val nextActivity = when {
                isFirstLaunch() -> OnboardingActivity::class.java
                isUserLoggedIn() -> MainActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, nextActivity))
            finish() // Prevent going back to SplashActivity
        }
    }

    private suspend fun isFirstLaunch(): Boolean {
        return withContext(Dispatchers.IO) {
            val isFirst = sharedPrefManager.isFirstLaunch()
            if (isFirst) {
                sharedPrefManager.setFirstLaunch(false)
            }
            isFirst
        }
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPrefManager.isLoggedIn() && FirebaseAuth.getInstance().currentUser != null
    }
}
