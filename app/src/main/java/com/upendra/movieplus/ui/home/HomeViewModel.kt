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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(trendingMovies = MovieUiState.Loading) }
            
            // Simulating API Call
            delay(2000)
            
            val mockMovies = listOf(
                Movie(1, "The Dark Knight", "/poster1.jpg", "/backdrop1.jpg", 9.0),
                Movie(2, "Inception", "/poster2.jpg", "/backdrop2.jpg", 8.8),
                Movie(3, "Interstellar", "/poster3.jpg", "/backdrop3.jpg", 8.6),
                Movie(4, "Oppenheimer", "/poster4.jpg", "/backdrop4.jpg", 8.5)
            )

            _uiState.update { 
                it.copy(
                    trendingMovies = MovieUiState.Success(mockMovies),
                    nowPlayingMovies = MovieUiState.Success(mockMovies.shuffled()),
                    popularMovies = MovieUiState.Success(mockMovies.reversed())
                )
            }
        }
    }
}
