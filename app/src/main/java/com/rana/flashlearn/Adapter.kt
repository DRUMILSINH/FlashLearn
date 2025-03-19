package com.rana.flashlearn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rana.flashlearn.data.CategoryEntity
import com.rana.flashlearn.data.FlashcardEntity
import com.rana.flashlearn.databinding.ItemCategoryBinding
import com.rana.flashlearn.databinding.ItemFlashcardBinding

class FlashcardAdapter(private var flashcards: MutableList<FlashcardEntity>) :
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

    // ✅ Efficiently update the RecyclerView when new flashcards are added
    fun updateData(newFlashcards: List<FlashcardEntity>) {
        val diffResult = DiffUtil.calculateDiff(FlashcardDiffCallback(flashcards, newFlashcards))
        flashcards.clear()
        flashcards.addAll(newFlashcards)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class FlashcardViewHolder(private val binding: ItemFlashcardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flashcard: FlashcardEntity) { // ✅ Fixed: Use FlashcardEntity
            binding.textViewQuestion.text = flashcard.question
            binding.textViewAnswer.text = flashcard.answer
            binding.textViewCategory.text = flashcard.category

            // ✅ Toggle card flip functionality
            var isShowingQuestion = true
            binding.cardViewFlashcard.setOnClickListener {
                if (isShowingQuestion) {
                    binding.textViewQuestion.visibility = View.GONE
                    binding.textViewAnswer.visibility = View.VISIBLE
                } else {
                    binding.textViewQuestion.visibility = View.VISIBLE
                    binding.textViewAnswer.visibility = View.GONE
                }
                isShowingQuestion = !isShowingQuestion
            }
        }
    }

    // ✅ Optimized updates using DiffUtil
    class FlashcardDiffCallback(
        private val oldList: List<FlashcardEntity>,
        private val newList: List<FlashcardEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class CategoryAdapter(private var categories: MutableList<CategoryEntity>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<CategoryEntity>) {
        val diffResult = DiffUtil.calculateDiff(CategoryDiffCallback(categories, newCategories))
        categories.clear()
        categories.addAll(newCategories)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryEntity) {
            binding.textViewCategoryName.text = category.categoryName
            binding.textViewCardCount.text = "${category.numberOfFlashcards} cards"
        }
    }

    class CategoryDiffCallback(
        private val oldList: List<CategoryEntity>,
        private val newList: List<CategoryEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }
}
