package com.rana.flashlearn.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.data.Flashcard
import com.rana.flashlearn.FlashcardAdapter
import com.rana.flashlearn.data.FlashcardDatabase
import com.rana.flashlearn.data.FlashcardEntity
import com.rana.flashlearn.data.toEntity
import com.rana.flashlearn.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val flashcardList = mutableListOf<FlashcardEntity>()
    private lateinit var flashcardAdapter: FlashcardAdapter
    private lateinit var database: FlashcardDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Database
        database = FlashcardDatabase.getDatabase(requireContext())

        // Initialize RecyclerView
        flashcardAdapter = FlashcardAdapter(flashcardList)
        binding.recyclerViewFlashcards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = flashcardAdapter
        }

        // Load flashcards from Room and Firestore
        loadFlashcards()

        // FAB Click - Open Add Flashcard Activity
        binding.fabAddFlashcard.setOnClickListener {
            startActivity(Intent(requireContext(), AddFlashcardActivity::class.java))
        }
    }

    private fun loadFlashcards() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewFlashcards.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            val roomFlashcards = database.flashcardDao().getAllFlashcards().toMutableList()

            val firestoreFlashcards = try {
                firestore.collection("flashcards")
                    .get()
                    .await()
                    .mapNotNull { document ->
                        document.toObject(Flashcard::class.java)?.toEntity()
                    }
            } catch (e: Exception) {
                null
            }

            // Merge Firestore and Room data, avoiding duplicates
            if (firestoreFlashcards != null) {
                for (flashcard in firestoreFlashcards) {
                    if (!roomFlashcards.any { it.id == flashcard.id }) {
                        roomFlashcards.add(flashcard)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                flashcardList.clear()
                flashcardList.addAll(roomFlashcards)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.progressBar.visibility = View.GONE
        if (flashcardList.isEmpty()) {
            binding.recyclerViewFlashcards.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.recyclerViewFlashcards.visibility = View.VISIBLE
            flashcardAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        loadFlashcards() // Reload flashcards when returning
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
