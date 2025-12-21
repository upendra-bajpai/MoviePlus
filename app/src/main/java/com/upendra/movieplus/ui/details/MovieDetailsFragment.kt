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
import com.upendra.movieplus.R
import com.upendra.movieplus.databinding.FragmentMovieDetailsBinding
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.chip.Chip
import android.graphics.Color
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
        
        setupToolbar()
        setupObservers()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    //TODO: alex optimize it
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state is MovieUiState.Loading) View.VISIBLE else View.GONE
                    binding.contentScroll.visibility = if (state is MovieUiState.Success) View.VISIBLE else View.GONE

                    if (state is MovieUiState.Success) {
                        displayMovie(state.data)
                    }
                }
            }
        }
    }

    //TODO: alex optimize it
    private fun displayMovie(movie: Movie) {
        binding.tvMovieTitle.text = movie.title
        binding.tvTagline.text = movie.tagline
        binding.tvTagline.visibility = if (movie.tagline.isNotEmpty()) View.VISIBLE else View.GONE
        
        binding.chipRating.text = String.format("%.1f", movie.rating)
        binding.chipDuration.text = movie.duration
        binding.chipYear.text = movie.releaseYear
        binding.tvSynopsis.text = movie.synopsis

        // Update Genres
        binding.chipGroupGenres.removeAllViews()
        movie.genres.forEach { genre ->
            if (genre.isNotBlank()) {
                val chip = Chip(requireContext()).apply {
                    text = genre
                    setChipBackgroundColorResource(R.color.surface)
                    setTextColor(Color.WHITE)
                    chipStrokeWidth = 0f
                }
                binding.chipGroupGenres.addView(chip)
            }
        }
        
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/original${movie.backdropPath}")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivBackdrop)

        val bookmarkIcon = if (movie.isBookmarked) 
            android.R.drawable.btn_star_big_on 
        else 
            android.R.drawable.btn_star_big_off
            
        binding.btnBookmark.setImageResource(bookmarkIcon)
            
        binding.btnBookmark.setOnClickListener {
            viewModel.toggleBookmark(movie)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
