package com.upendra.movieplus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upendra.movieplus.R
import com.upendra.movieplus.databinding.FragmentHomeBinding
import com.upendra.movieplus.ui.adapter.MovieAdapter
import com.upendra.movieplus.ui.adapter.TrendingAdapter
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var nowPlayingAdapter: MovieAdapter
    private lateinit var popularAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupRecyclerViews() {
        nowPlayingAdapter = MovieAdapter { navigateToDetails(it) }
        popularAdapter = MovieAdapter { navigateToDetails(it) }

        binding.rvNowPlaying.apply {
            adapter = nowPlayingAdapter
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = layoutManager as LinearLayoutManager
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 5) {
                        viewModel.loadMoreNowPlaying()
                    }
                }
            })
        }

        binding.rvPopular.apply {
            adapter = popularAdapter
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = layoutManager as LinearLayoutManager
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    if (lastVisibleItem >= totalItemCount - 5) {
                        viewModel.loadMorePopular()
                    }
                }
            })
        }
    }

    private fun navigateToDetails(movie: Movie) {
        val bundle = Bundle().apply { putInt("movieId", movie.id) }
        findNavController().navigate(R.id.action_homeFragment_to_movieDetailsFragment, bundle)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    handleTrending(state.trendingMovies)
                    handleNowPlaying(state.nowPlayingMovies)
                    handlePopular(state.popularMovies)
                }
            }
        }
    }

    private fun handleTrending(state: MovieUiState<List<Movie>>) {
        when (state) {
            is MovieUiState.Loading -> {
                binding.shimmerView.visibility = View.VISIBLE
                binding.shimmerView.startShimmer()
            }
            is MovieUiState.Success -> {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
                binding.trendingPager.adapter = TrendingAdapter(state.data) { navigateToDetails(it) }
            }
            is MovieUiState.Error -> {
                binding.shimmerView.stopShimmer()
                binding.shimmerView.visibility = View.GONE
            }
        }
    }

    private fun handleNowPlaying(state: MovieUiState<List<Movie>>) {
        if (state is MovieUiState.Success) {
            nowPlayingAdapter.submitList(state.data)
        }
    }

    private fun handlePopular(state: MovieUiState<List<Movie>>) {
        if (state is MovieUiState.Success) {
            popularAdapter.submitList(state.data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
