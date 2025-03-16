package com.rana.flashlearn.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.Category
import com.rana.flashlearn.CategoryAdapter
import com.rana.flashlearn.databinding.FragmentCategoriesBinding
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var firestore: FirebaseFirestore
    private var oldCategories: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        binding.recyclerViewCategories.layoutManager = GridLayoutManager(context, 2)
        loadCategories()
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerViewCategories.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.tvEmpty.visibility = View.GONE

            try {
                val newCategories = withContext(Dispatchers.IO) { getCategoriesFromFirestore() }

                if (newCategories.isNotEmpty()) {
                    val diffResult = DiffUtil.calculateDiff(CategoryDiffCallback(oldCategories, newCategories))
                    categoryAdapter = CategoryAdapter(newCategories)
                    binding.recyclerViewCategories.adapter = categoryAdapter
                    diffResult.dispatchUpdatesTo(categoryAdapter)
                    binding.recyclerViewCategories.visibility = View.VISIBLE
                    oldCategories = newCategories
                } else {
                    binding.tvEmpty.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Failed to load categories: ${e.message}"
                Log.e("CategoriesFragment", "Firestore error: ${e.message}", e)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun getCategoriesFromFirestore(): List<Category> {
        return suspendCoroutine<List<Category>> { continuation ->
            firestore.collection("categories")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val categories = mutableListOf<Category>()
                    for (document in querySnapshot.documents) {
                        try {
                            val category = Category(
                                document.id,
                                document.getString("categoryName") ?: "",
                                document.getLong("numberOfFlashcards")?.toInt() ?: 0
                            )
                            categories.add(category)
                        } catch (e: Exception) {
                            Log.e("CategoriesFragment", "Document parsing error: ${e.message}", e)
                        }
                    }
                    continuation.resume(categories)
                }
                .addOnFailureListener { e ->
                    Log.e("CategoriesFragment", "Firestore get error: ${e.message}", e)
                    continuation.resumeWithException(e)
                }
        }
    }

    class CategoryDiffCallback(
        private val oldList: List<Category>,
        private val newList: List<Category>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
