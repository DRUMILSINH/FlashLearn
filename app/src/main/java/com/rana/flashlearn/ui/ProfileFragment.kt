package com.rana.flashlearn.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rana.flashlearn.databinding.FragmentProfileBinding
import com.rana.flashlearn.sync.SyncWorker
import java.text.SimpleDateFormat
import java.util.*

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

        // Load user profile and sync status
        loadUserProfile()
        updateLastSyncTime()

        // Sync Now Button
        binding.buttonSyncNow.setOnClickListener {
            triggerManualSync()
        }
    }

    private fun loadUserProfile() {
        val sharedPrefs = requireActivity().getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("username", "User") ?: "User"
        val email = sharedPrefs.getString("email", "email@example.com") ?: "email@example.com"

        binding.textViewUsername.text = username
        binding.textViewEmail.text = email
        binding.textViewTotalCards.text = "45 Flashcards" // Fetch from DB
        binding.textViewCompletedQuizzes.text = "12 Quizzes Completed" // Fetch from DB
    }

    private fun triggerManualSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(requireContext()).enqueue(syncRequest)

        Toast.makeText(requireContext(), "Sync Started...", Toast.LENGTH_SHORT).show()

        // Save Sync Time in SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        editor.putString("last_sync_time", currentTime)
        editor.apply()

        updateLastSyncTime()
    }

    private fun updateLastSyncTime() {
        val sharedPrefs = requireActivity().getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
        val lastSyncTime = sharedPrefs.getString("last_sync_time", "Never")
        binding.textViewLastSync.text = "Last Sync: $lastSyncTime"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
