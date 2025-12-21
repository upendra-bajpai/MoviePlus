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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.upendra.movieplus.data.repository.MovieRepository

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

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
            
            val results = repository.searchMovies(query)
            _searchState.value = MovieUiState.Success(results)
        }
    }
}
