package com.rana.flashlearn.data

import com.rana.flashlearn.data.FlashcardEntity
import java.util.Date

data class Flashcard(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val category: String = "",
    val repetitions: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReviewDate: Date = Date()
)

// Convert Firestore Flashcard to Room FlashcardEntity
fun Flashcard.toEntity(): FlashcardEntity {
    return FlashcardEntity(
        id = this.id,
        question = this.question,
        answer = this.answer,
        category = this.category,
        repetitions = this.repetitions,
        easeFactor = this.easeFactor,
        nextReviewDate = this.nextReviewDate
    )
}
