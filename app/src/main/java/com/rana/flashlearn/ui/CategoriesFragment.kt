package com.rana.flashlearn.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.CategoryAdapter
import com.rana.flashlearn.data.AppDatabase
import com.rana.flashlearn.data.CategoryEntity
import com.rana.flashlearn.databinding.FragmentCategoriesBinding
import com.rana.flashlearn.ui.dialogs.AddCategoryDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var firestore: FirebaseFirestore
    private val categoryDao by lazy { AppDatabase.getDatabase(requireContext()).categoryDao() }

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
        categoryAdapter = CategoryAdapter(mutableListOf()) // ✅ Pass empty mutable list

        binding.recyclerViewCategories.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewCategories.adapter = categoryAdapter

        binding.fabAddCategory.setOnClickListener {
            AddCategoryDialog(categoryDao).show(parentFragmentManager, "AddCategoryDialog")
        }

        loadCategories()
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerViewCategories.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.emptyStateContainer.visibility = View.GONE

            try {
                val roomCategories = withContext(Dispatchers.IO) { categoryDao.getAllCategories() }
                if (roomCategories.isNotEmpty()) {
                    updateRecyclerView(roomCategories)
                } else {
                    val firestoreCategories = getCategoriesFromFirestore()
                    withContext(Dispatchers.IO) {
                        categoryDao.insertCategories(firestoreCategories)
                    }
                    updateRecyclerView(firestoreCategories)
                }

            } catch (e: Exception) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Failed to load categories: ${e.message}"
                Log.e("CategoriesFragment", "Error: ${e.message}", e)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateRecyclerView(categories: List<CategoryEntity>) {
        categoryAdapter.updateData(categories) // ✅ Use correct function
        binding.recyclerViewCategories.visibility = View.VISIBLE
        binding.emptyStateContainer.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
    }

    private suspend fun getCategoriesFromFirestore(): List<CategoryEntity> {
        val categories = mutableListOf<CategoryEntity>()
        val snapshot = firestore.collection("categories").get().await()

        for (document in snapshot.documents) {
            try {
                val category = CategoryEntity(
                    id = document.id,
                    categoryName = document.getString("categoryName") ?: "",
                    numberOfFlashcards = document.getLong("numberOfFlashcards")?.toInt() ?: 0
                )
                categories.add(category)
            } catch (e: Exception) {
                Log.e("CategoriesFragment", "Firestore document error: ${e.message}", e)
            }
        }
        return categories
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
