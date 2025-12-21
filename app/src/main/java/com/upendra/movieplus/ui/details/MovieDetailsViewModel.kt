package com.upendra.movieplus.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.upendra.movieplus.data.repository.MovieRepository

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movieDetails = MutableStateFlow<MovieUiState<Movie>>(MovieUiState.Loading)
    val movieDetails: StateFlow<MovieUiState<Movie>> = _movieDetails.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = MovieUiState.Loading
            
            // In a real app, you might fetch from repository here
            // For now, using mock or getting from cached movies in repository
            // repository.getMovieDetails(movieId) ... 
        }
    }

    fun toggleBookmark(movie: Movie) {
        viewModelScope.launch {
            repository.toggleBookmark(movie.id, !movie.isBookmarked)
        }
    }
}
