package com.rana.flashlearn.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import androidx.lifecycle.lifecycleScope
import com.rana.flashlearn.Flashcard
import com.rana.flashlearn.FlashcardAdapter
import com.rana.flashlearn.databinding.FragmentHomeBinding
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.rana.flashlearn.SharedPrefManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var flashcardAdapter: FlashcardAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val flashcardsCollection = firestore.collection("flashcards")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.recyclerViewFlashcards.layoutManager = LinearLayoutManager(context)

        // Load flashcards asynchronously
        loadFlashcards()
    }

    private fun loadFlashcards() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerViewFlashcards.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.tvEmpty.visibility = View.GONE

            try {
                // Get user ID from SharedPreferences
                val userId = SharedPrefManager.getInstance(requireContext()).getUserId()

                // Load flashcards for the current user
                val flashcards = withContext(Dispatchers.IO) {
                    getFlashcardsFromFirebase(userId)
                }

                if (flashcards.isNotEmpty()) {
                    flashcardAdapter = FlashcardAdapter(flashcards)
                    binding.recyclerViewFlashcards.adapter = flashcardAdapter
                    binding.recyclerViewFlashcards.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Failed to load flashcards: ${e.message}"
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun getFlashcardsFromFirebase(userId: String?): List<Flashcard> = suspendCoroutine { continuation ->
        if (userId == null) {
            continuation.resume(emptyList())
            return@suspendCoroutine
        }

        // Query flashcards where createdBy field equals the current userId
        flashcardsCollection.whereEqualTo("createdBy", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val flashcardList = parseFlashcardsFromFirestore(querySnapshot)
                continuation.resume(flashcardList)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    private fun parseFlashcardsFromFirestore(querySnapshot: QuerySnapshot): List<Flashcard> {
        val flashcards = mutableListOf<Flashcard>()

        for (document in querySnapshot.documents) {
            val id = document.id
            val question = document.getString("question") ?: ""
            val answer = document.getString("answer") ?: ""
            val category = document.getString("category") ?: ""

            flashcards.add(Flashcard(id, question, answer, category))
        }

        return flashcards
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
