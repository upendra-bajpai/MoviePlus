package com.upendra.movieplus.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.HomeUiState
import com.upendra.movieplus.ui.model.Movie
import com.upendra.movieplus.ui.model.MovieUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.upendra.movieplus.data.repository.MovieRepository

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.update { it.copy(
            trendingMovies = MovieUiState.Loading,
            nowPlayingMovies = MovieUiState.Loading,
            popularMovies = MovieUiState.Loading
        )}

        viewModelScope.launch {
            try {
                repository.refreshTrending()
                repository.refreshNowPlaying()
                repository.refreshPopular()
            } catch (e: Exception) {
                // If DB is empty, show error. If not, the collected flows will show old data.
            }
        }

        // Collect Trending
        viewModelScope.launch {
            repository.getTrending().collect { movies ->
                if (movies.isNotEmpty()) {
                    _uiState.update { it.copy(trendingMovies = MovieUiState.Success(movies)) }
                }
            }
        }

        // Collect Now Playing
        viewModelScope.launch {
            repository.getNowPlaying().collect { movies ->
                if (movies.isNotEmpty()) {
                    _uiState.update { it.copy(nowPlayingMovies = MovieUiState.Success(movies)) }
                }
            }
        }

        // Collect Trending
        viewModelScope.launch {
            repository.getPopular().collect { movies ->
                if (movies.isNotEmpty()) {
                    _uiState.update { it.copy(popularMovies = MovieUiState.Success(movies)) }
                }
            }
        }
    }
}
