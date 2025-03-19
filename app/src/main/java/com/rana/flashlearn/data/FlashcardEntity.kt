package com.rana.flashlearn.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID
import com.rana.flashlearn.Flashcard

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val question: String,
    val answer: String,
    val category: String,
    val repetitions: Int = 0,
    val easeFactor: Double = 2.5,
    val nextReviewDate: Date = Date()
)

fun FlashcardEntity.toFlashcard(): com.rana.flashlearn.data.Flashcard {
    return Flashcard(
        id = this.id,
        question = this.question,
        answer = this.answer,
        category = this.category,
        repetitions = this.repetitions,
        easeFactor = this.easeFactor,
        nextReviewDate = this.nextReviewDate
    )
}
