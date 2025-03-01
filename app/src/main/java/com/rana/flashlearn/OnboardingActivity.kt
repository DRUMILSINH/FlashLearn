package com.rana.flashlearn


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.rana.flashlearn.ActivityOnboardingBinding
import com.rana.flashlearn.ui.auth.LoginActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnboardingScreens()
        setupButtonListeners()
    }

    private fun setupOnboardingScreens() {
        val onboardingData = listOf(
            OnboardingItem("Welcome to FlashLearn!", "Boost your learning with smart flashcards.", com.yourpackage.R.drawable.onboarding1),
            OnboardingItem("Track Progress", "Monitor your performance with analytics.", com.yourpackage.R.drawable.onboarding2),
            OnboardingItem("Stay Motivated", "Earn rewards and stay consistent!", com.yourpackage.R.drawable.onboarding3)
        )

        onboardingAdapter = OnboardingAdapter(this, onboardingData)
        binding.viewPager.adapter = onboardingAdapter

        // Connect TabLayout with ViewPager2 for page indicators
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
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
