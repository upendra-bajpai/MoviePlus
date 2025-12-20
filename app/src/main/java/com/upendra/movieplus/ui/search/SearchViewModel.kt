package com.upendra.movieplus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _searchState = MutableStateFlow<MovieUiState<List<Movie>>>(MovieUiState.Success(emptyList()))
    val searchState: StateFlow<MovieUiState<List<Movie>>> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchState.value = MovieUiState.Success(emptyList())
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchState.value = MovieUiState.Loading
            delay(300) // Debounce
            
            // Simulating API Call
            val mockResults = listOf(
                Movie(1, "The Dark Knight", "/poster1.jpg", "/backdrop1.jpg", 9.0),
                Movie(2, "Inception", "/poster2.jpg", "/backdrop2.jpg", 8.8)
            ).filter { it.title.contains(query, ignoreCase = true) }

            _searchState.value = MovieUiState.Success(mockResults)
        }
    }
}
