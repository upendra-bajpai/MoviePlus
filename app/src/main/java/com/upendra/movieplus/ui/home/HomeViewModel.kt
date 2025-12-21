package com.upendra.movieplus.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.HomeUiState
import com.upendra.movieplus.ui.model.MovieUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.upendra.movieplus.data.repository.MovieRepository
import kotlinx.coroutines.flow.combine

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTrending(),
        repository.getNowPlaying(),
        repository.getPopular()
    ){ trending, nowPlaying, popular ->
        HomeUiState(
            trendingMovies = if (trending.isEmpty()) MovieUiState.Loading else MovieUiState.Success(trending),
            nowPlayingMovies = if(nowPlaying.isEmpty()) MovieUiState.Loading else MovieUiState.Success(nowPlaying),
            popularMovies = if(popular.isEmpty()) MovieUiState.Loading else MovieUiState.Success(popular)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                launch { repository.refreshTrending() }
                launch { repository.refreshNowPlaying() }
                launch { repository.refreshPopular() }
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error loading home data: ${e.message}")
            }
        }
    }
}
