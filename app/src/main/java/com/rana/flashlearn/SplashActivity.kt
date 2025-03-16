package com.rana.flashlearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPrefManager = SharedPrefManager.getInstance(this)

        lifecycleScope.launch {
            delay(2000)

            val nextActivity = when {
                isFirstLaunch() -> OnboardingActivity::class.java
                isUserLoggedIn() -> MainActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }
    }

    private fun isFirstLaunch(): Boolean {
        val isFirst = sharedPrefManager.isFirstLaunch()
        if (isFirst) {
            sharedPrefManager.setFirstLaunch(false)
        }
        return isFirst
    }

    private fun isUserLoggedIn(): Boolean {
        return sharedPrefManager.isLoggedIn() && FirebaseAuth.getInstance().currentUser != null
    }
}