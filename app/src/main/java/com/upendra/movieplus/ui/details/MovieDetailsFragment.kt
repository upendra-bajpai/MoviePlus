package com.upendra.movieplus.ui.details

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.upendra.movieplus.databinding.FragmentMovieDetailsBinding
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val movieId = arguments?.getInt("movieId") ?: return
        viewModel.loadMovieDetails(movieId)
        
        setupToolbar()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetails.collect { state ->
                    if (state is MovieUiState.Success) {
                        displayMovie(state.data)
                    }
                }
            }
        }
    }

    private fun displayMovie(movie: Movie) {
        binding.tvMovieTitle.text = movie.title
        binding.chipRating.text = movie.rating.toString()
        binding.chipDuration.text = movie.duration
        binding.chipYear.text = movie.releaseYear
        binding.tvSynopsis.text = movie.synopsis
        
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/original${movie.backdropPath}")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivBackdrop)
            
        binding.btnBookmark.setOnClickListener {
            viewModel.toggleBookmark(movie)
            // Show haptic feedback (logic would go here)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
