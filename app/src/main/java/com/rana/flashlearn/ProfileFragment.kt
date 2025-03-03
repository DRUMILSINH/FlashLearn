package com.rana.flashlearn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rana.flashlearn.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load user profile data
        loadUserProfile()

        // Set up logout button click listener
        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile() {
        // Get user data from SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "User") ?: "User"
        val email = sharedPrefs.getString("email", "email@example.com") ?: "email@example.com"

        // Display user data
        binding.textViewUsername.text = username
        binding.textViewEmail.text = email
        binding.textViewTotalCards.text = "45 Flashcards" // Replace with actual count from database
        binding.textViewCompletedQuizzes.text = "12 Quizzes Completed" // Replace with actual count
    }

    private fun logout() {
        // Clear SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        // Navigate to LoginActivity
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}