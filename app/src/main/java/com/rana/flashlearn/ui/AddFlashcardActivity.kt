package com.rana.flashlearn.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.data.FlashcardDatabase
import com.rana.flashlearn.data.FlashcardEntity
import com.rana.flashlearn.databinding.ActivityAddFlashcardBinding
import kotlinx.coroutines.launch

class AddFlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFlashcardBinding
    private lateinit var database: FlashcardDatabase
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FlashcardDatabase.getDatabase(this)

        // Handle Save Button Click
        binding.btnSaveFlashcard.setOnClickListener {
            saveFlashcard()
        }
    }

    private fun saveFlashcard() {
        val question = binding.etQuestion.text.toString().trim()
        val answer = binding.etAnswer.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()

        if (question.isEmpty() || answer.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val flashcard = FlashcardEntity(
            question = question,
            answer = answer,
            category = category
        )

        lifecycleScope.launch {
            database.flashcardDao().insertFlashcard(flashcard)
        }

        firestore.collection("flashcards").document(flashcard.id)
            .set(flashcard)
            .addOnSuccessListener {
                Toast.makeText(this, "Flashcard added successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add flashcard", Toast.LENGTH_SHORT).show()
            }
    }
}
