package com.rana.flashlearn

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Minimal UI for branding

        sharedPrefManager = SharedPrefManager.getInstance(this)

        // Delay for smooth transition (2 seconds)
        Handler(Looper.getMainLooper()).postDelayed({
            val nextActivity = when {
                isFirstLaunch() -> OnboardingActivity::class.java
                isUserLoggedIn() -> MainActivity::class.java
                else -> LoginActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()
        }, 2000) // 2-second delay
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


