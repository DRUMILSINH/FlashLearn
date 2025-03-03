package com.rana.flashlearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rana.flashlearn.Flashcard
import com.rana.flashlearn.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var flashcardAdapter: FlashcardAdapter

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
        flashcardAdapter = FlashcardAdapter(getFlashcards())
        binding.recyclerViewFlashcards.adapter = flashcardAdapter
    }

    // Sample data - replace with actual data from Firebase/database
    private fun getFlashcards(): List<Flashcard> {
        return listOf(
            Flashcard("1", "What is Kotlin?", "A modern programming language for Android", "Programming"),
            Flashcard("2", "What is a Fragment?", "A reusable component representing a portion of UI", "Android"),
            Flashcard("3", "What is Firebase?", "A platform for developing mobile and web applications", "Cloud")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}