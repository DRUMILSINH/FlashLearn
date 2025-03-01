package com.rana.flashlearn

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(
    fragment: Fragment,
    private val onboardingData: List<OnboardingItem>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = onboardingData.size

    override fun createFragment(position: Int): Fragment {
        val item = onboardingData[position]
        return OnboardingFragment.newInstance(item.title, item.description, item.imageRes)
    }
}

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int
)