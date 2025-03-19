package com.rana.flashlearn.sync

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getDatabase(context)
    private val flashcardDao = database.flashcardDao()
    private val categoryDao = database.categoryDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            syncFlashcards()
            syncCategories()

            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val sharedPrefs = context.getSharedPreferences("FlashLearnPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putString("last_sync_time", currentTime).apply()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Sync Completed!", Toast.LENGTH_SHORT).show()
            }

            Log.d("SyncWorker", "Sync completed successfully!")
            return@withContext Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed: ${e.message}", e)
            return@withContext Result.failure()
        }
    }

    private suspend fun syncFlashcards() {
        val flashcards = flashcardDao.getAllFlashcards()
        for (flashcard in flashcards) {
            val flashcardMap = mapOf(
                "id" to flashcard.id,
                "question" to flashcard.question,
                "answer" to flashcard.answer,
                "category" to flashcard.category
            )
            firestore.collection("flashcards").document(flashcard.id).set(flashcardMap)
        }
        Log.d("SyncWorker", "Flashcards synced successfully!")
    }

    private suspend fun syncCategories() {
        val categories = categoryDao.getAllCategories()
        for (category in categories) {
            val categoryMap = mapOf(
                "id" to category.id,
                "categoryName" to category.categoryName,
                "numberOfFlashcards" to category.numberOfFlashcards
            )
            firestore.collection("categories").document(category.id).set(categoryMap)
        }
        Log.d("SyncWorker", "Categories synced successfully!")
    }
}
