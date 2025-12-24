package com.upendra.movieplus.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.data.repository.MovieRepository
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import com.upendra.movieplus.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    private val _uiState = MutableStateFlow<MovieUiState<Movie>>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState<Movie>> = _uiState.asStateFlow()

    init {
        fetchMovieDetails()
    }

    private fun fetchMovieDetails() {
        if (movieId == -1) {
            _uiState.value = MovieUiState.Error("Invalid Movie ID")
            return
        }
        viewModelScope.launch {
            repository.getMovieDetails(movieId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = MovieUiState.Loading
                    }
                    is Resource.Success -> {
                        _uiState.value = MovieUiState.Success(resource.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = MovieUiState.Error(resource.message)
                    }
                }
            }
        }
    }

    fun toggleBookmark(movie: Movie) {
        viewModelScope.launch {
            repository.toggleBookmark(movie.id, !movie.isBookmarked)
                fetchMovieDetails()
        }
    }
}
