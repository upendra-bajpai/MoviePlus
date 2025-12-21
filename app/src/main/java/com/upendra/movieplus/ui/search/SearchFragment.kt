package com.upendra.movieplus.ui.search

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.setText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.upendra.movieplus.R
import com.upendra.movieplus.databinding.FragmentSearchBinding
import com.upendra.movieplus.ui.adapter.MovieAdapter
import com.upendra.movieplus.ui.model.MovieUiState
import com.upendra.movieplus.utils.toStringTrimmed
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearch()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter { movie ->
            val bundle = Bundle().apply { putInt("movieId", movie.id) }
            findNavController().navigate(R.id.action_searchFragment_to_movieDetailsFragment, bundle)
        }
        binding.rvSearchResults.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { v, actionId, event ->
            val query = binding.searchView.text.toStringTrimmed()
            binding.searchBar.setText(query)
            binding.searchView.hide()
            viewModel.searchMovies(query)
            false
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchState.collect { state ->
                    when (state) {
                        is MovieUiState.Loading -> {
                            binding.layoutEmptyState.visibility = View.GONE
                            // binding.progressBar.visibility = View.VISIBLE
                        }

                        is MovieUiState.Success -> {
                            adapter.submitList(state.data)

                            if (state.data.isEmpty()) {
                                binding.layoutEmptyState.visibility = View.VISIBLE
                                binding.rvSearchResults.visibility = View.GONE
                            } else {
                                binding.layoutEmptyState.visibility = View.GONE
                                binding.rvSearchResults.visibility = View.VISIBLE
                            }
                        }

                        is MovieUiState.Error -> {
                            binding.layoutEmptyState.visibility = View.VISIBLE
                            binding.rvSearchResults.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

