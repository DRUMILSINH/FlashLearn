package com.rana.flashlearn.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rana.flashlearn.AuthRepository
import com.rana.flashlearn.LoginActivity
import com.rana.flashlearn.R
import com.rana.flashlearn.SharedPrefManager
import com.rana.flashlearn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefManager = SharedPrefManager.getInstance(this)
        authRepository = AuthRepository() // Ensure correct initialization

        // Check if the user is logged in
        if (!authRepository.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Set up toolbar
        setSupportActionBar(binding.toolbar)

        // Load default fragment (only if there’s no saved state)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Set up bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_categories -> CategoriesFragment()
                R.id.navigation_profile -> ProfileFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) } != null
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Allows back navigation
            .commit()
    }
}
