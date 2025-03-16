package com.rana.flashlearn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.rana.flashlearn.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefManager = SharedPrefManager.getInstance(this)

        setupOnboardingScreens()
        setupButtonListeners()
    }

    private fun setupOnboardingScreens() {
        onboardingAdapter = OnboardingAdapter(this)
        binding.viewPager.adapter = onboardingAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupButtonListeners() {
        binding.btnNext.setOnClickListener {
            val nextItem = binding.viewPager.currentItem + 1
            if (nextItem < onboardingAdapter.itemCount) {
                binding.viewPager.currentItem = nextItem
            } else {
                navigateToLogin()
            }
        }

        binding.tvSkip.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        sharedPrefManager.setFirstLaunch(false) // Save onboarding state
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}