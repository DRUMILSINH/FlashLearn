package com.rana.flashlearn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rana.flashlearn.Category
import com.rana.flashlearn.Flashcard
import com.rana.flashlearn.databinding.ItemCategoryBinding
import com.rana.flashlearn.databinding.ItemFlashcardBinding

class FlashcardAdapter(private val flashcards: List<Flashcard>) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val binding = ItemFlashcardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlashcardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(flashcards[position])
    }

    override fun getItemCount() = flashcards.size

    inner class FlashcardViewHolder(private val binding: ItemFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flashcard: Flashcard) {
            binding.textViewQuestion.text = flashcard.question
            binding.textViewAnswer.text = flashcard.answer
            binding.textViewCategory.text = flashcard.category

            // Toggle card flip functionality
            var isShowingQuestion = true
            binding.cardViewFlashcard.setOnClickListener {
                if (isShowingQuestion) {
                    binding.textViewQuestion.visibility = ViewGroup.GONE
                    binding.textViewAnswer.visibility = ViewGroup.VISIBLE
                } else {
                    binding.textViewQuestion.visibility = ViewGroup.VISIBLE
                    binding.textViewAnswer.visibility = ViewGroup.GONE
                }
                isShowingQuestion = !isShowingQuestion
            }
        }
    }
}

class CategoryAdapter(private val categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.textViewCategoryName.text = category.name
            binding.textViewCardCount.text = "${category.cardCount} cards"
        }
    }
}