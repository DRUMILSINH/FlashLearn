package com.rana.flashlearn.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.data.FlashcardEntity
import com.rana.flashlearn.repository.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.work.ListenableWorker

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val flashcardRepository: FlashcardRepository
) : CoroutineWorker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            val flashcards = flashcardRepository.getAllFlashcards()
            for (flashcard in flashcards) {
                val flashcardMap = mapOf(
                    "id" to flashcard.id,
                    "question" to flashcard.question,
                    "answer" to flashcard.answer,
                    "category" to flashcard.category
                )
                firestore.collection("flashcards").document(flashcard.id).set(flashcardMap)
            }
            ListenableWorker.Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during synchronization: ${e.message}", e)
            ListenableWorker.Result.failure()

        }
    }
}
