package com.rana.flashlearn.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.util.UUID

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







