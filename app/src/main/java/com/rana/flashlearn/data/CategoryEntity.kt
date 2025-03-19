package com.rana.flashlearn.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val categoryName: String,
    val numberOfFlashcards: Int
)
