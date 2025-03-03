package com.rana.flashlearn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.rana.flashlearn.Category
import com.rana.flashlearn.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView with GridLayoutManager
        binding.recyclerViewCategories.layoutManager = GridLayoutManager(context, 2)
        categoryAdapter = CategoryAdapter(getCategories())
        binding.recyclerViewCategories.adapter = categoryAdapter
    }

    // Sample data - replace with actual data from Firebase/database
    private fun getCategories(): List<Category> {
        return listOf(
            Category("1", "Programming", 15),
            Category("2", "Android", 8),
            Category("3", "Cloud", 10),
            Category("4", "Web Development", 12)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}