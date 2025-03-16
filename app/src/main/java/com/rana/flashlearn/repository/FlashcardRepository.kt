package com.rana.flashlearn.repository

import com.rana.flashlearn.data.FlashcardDao
import com.rana.flashlearn.data.FlashcardEntity
import com.rana.flashlearn.utils.SpacedRepetitionUtil

import java.util.Date

class FlashcardRepository(private val flashcardDao: FlashcardDao) {

    suspend fun addFlashcard(flashcard: FlashcardEntity) {
        flashcardDao.insertFlashcard(flashcard)
    }

    suspend fun updateFlashcard(flashcard: FlashcardEntity) {
        flashcardDao.updateFlashcard(flashcard)
    }

    suspend fun deleteFlashcard(flashcard: FlashcardEntity) {
        flashcardDao.deleteFlashcard(flashcard)
    }

    suspend fun getFlashcardById(id: String): FlashcardEntity? {
        return flashcardDao.getFlashcardById(id)
    }

    suspend fun getAllFlashcards(): List<FlashcardEntity> {
        return flashcardDao.getAllFlashcards()
    }

    suspend fun reviewFlashcard(flashcard: FlashcardEntity, quality: Int) {
        val currentDate = Date()
        val (nextReviewDate, newEaseFactor) = SpacedRepetitionUtil.calculateNextReviewDate(
            currentDate,
            quality,
            flashcard.repetitions,
            flashcard.easeFactor
        )

        val updatedFlashcard = flashcard.copy(
            nextReviewDate = nextReviewDate,
            easeFactor = newEaseFactor,
            repetitions = flashcard.repetitions + 1
        )

        flashcardDao.updateFlashcard(updatedFlashcard)
    }
}
