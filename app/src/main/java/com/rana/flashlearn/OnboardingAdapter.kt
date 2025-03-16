package com.rana.flashlearn

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rana.flashlearn.ui.OnboardingActivity

class OnboardingAdapter(activity: OnboardingActivity) : FragmentStateAdapter(activity) {

    private val onboardingData = listOf(
        Triple("Welcome to FlashLearn!", "Boost your learning with smart flashcards.", R.drawable.onboarding1),
        Triple("Track Progress", "Monitor your performance with analytics.", R.drawable.onboarding2),
        Triple("Stay Motivated", "Earn rewards and stay consistent!", R.drawable.onboarding3)
    )

    override fun getItemCount(): Int = onboardingData.size

    override fun createFragment(position: Int): Fragment {
        val (title, description, imageRes) = onboardingData[position]
        return OnboardingFragment.newInstance(title, description, imageRes)
    }
}
