package com.rana.flashlearn.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.rana.flashlearn.data.CategoryEntity
import com.rana.flashlearn.data.CategoryDao
import com.rana.flashlearn.databinding.DialogAddCategoryBinding
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddCategoryDialog(private val categoryDao: CategoryDao) : DialogFragment() {

    private var _binding: DialogAddCategoryBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCategoryBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        binding.btnSaveCategory.setOnClickListener { saveCategory() }
        binding.btnCancel.setOnClickListener { dismiss() }

        return dialog
    }

    private fun saveCategory() {
        val categoryName = binding.etCategoryName.text.toString().trim()

        if (categoryName.isEmpty()) {
            binding.etCategoryName.error = "Category name cannot be empty!"
            return
        }

        val newCategory = CategoryEntity(
            id = System.currentTimeMillis().toString(), // Generate unique ID
            categoryName = categoryName,
            numberOfFlashcards = 0
        )

        binding.btnSaveCategory.isEnabled = false // Disable button to prevent duplicate clicks

        // Save to Firestore
        firestore.collection("categories")
            .document(newCategory.id)
            .set(newCategory)
            .addOnSuccessListener {
                lifecycleScope.launch {
                    categoryDao.insertCategory(newCategory) // Save to Room Database (Offline Mode)
                }
                Toast.makeText(requireContext(), "Category added successfully!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add category", Toast.LENGTH_SHORT).show()
                binding.btnSaveCategory.isEnabled = true // Re-enable button on failure
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
