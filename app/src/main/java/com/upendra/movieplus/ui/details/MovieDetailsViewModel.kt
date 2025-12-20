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

class MovieDetailsViewModel : ViewModel() {

    private val _movieDetails = MutableStateFlow<MovieUiState<Movie>>(MovieUiState.Loading)
    val movieDetails: StateFlow<MovieUiState<Movie>> = _movieDetails.asStateFlow()

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetails.value = MovieUiState.Loading
            delay(1000)
            
            // Mock Data
            val movie = Movie(
                id = movieId,
                title = "Oppenheimer",
                posterPath = "/poster4.jpg",
                backdropPath = "/backdrop4.jpg",
                rating = 8.5,
                duration = "180 mins",
                releaseYear = "2023",
                synopsis = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb. A cinematic masterpiece exploring the complexity of human ambition and the consequences of absolute power."
            )
            _movieDetails.value = MovieUiState.Success(movie)
        }
    }

    fun toggleBookmark(movie: Movie) {
        // Handle bookmark logic with Room
    }
}
