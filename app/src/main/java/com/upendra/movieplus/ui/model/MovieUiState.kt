package com.upendra.movieplus.ui.model

sealed class MovieUiState<out T> {
    object Loading : MovieUiState<Nothing>()
    data class Success<out T>(val data: T) : MovieUiState<T>()
    data class Error(val message: String) : MovieUiState<Nothing>()
}

data class HomeUiState(
    val trendingMovies: MovieUiState<List<Movie>> = MovieUiState.Loading,
    val nowPlayingMovies: MovieUiState<List<Movie>> = MovieUiState.Loading,
    val popularMovies: MovieUiState<List<Movie>> = MovieUiState.Loading
)

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val rating: Double,
    val duration: String = "",
    val releaseYear: String = "",
    val synopsis: String = "",
    val genres: List<String> = emptyList(),
    var isBookmarked: Boolean = false
)
