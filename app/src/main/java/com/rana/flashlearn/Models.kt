package com.rana.flashlearn

data class Flashcard(
    val id: String,
    val question: String,
    val answer: String,
    val category: String
)

data class Category(
    val id: String,
    val name: String,
    val cardCount: Int
)