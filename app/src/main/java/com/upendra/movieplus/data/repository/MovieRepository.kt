package com.upendra.movieplus.data.repository

import android.util.Log
import com.upendra.movieplus.data.local.MovieDao
import com.upendra.movieplus.data.local.entity.MovieEntity
import com.upendra.movieplus.data.remote.MovieApiService
import com.upendra.movieplus.data.remote.dto.MovieDto
import com.upendra.movieplus.ui.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao
) {

    fun getNowPlaying(): Flow<List<Movie>> {
        return movieDao.getMoviesByCategory("now_playing")
            .map { entities -> entities.map { it.toUiModel() } }
            .onEach { movies ->
                if (movies.isEmpty()) {
                    refreshNowPlaying()
                }
            }
    }

    suspend fun refreshNowPlaying() {
        try {
            val response = apiService.getNowPlaying()
            if (response.isSuccessful) {
                response.body()?.results?.let { dtos ->
                    val entities = dtos.map { it.toEntity("now_playing") }
                    movieDao.upsertAll(entities)
                }
            }
        } catch (e: Exception) {
             Log.d("MovieRepository", "Error refreshing now playing: ${e.message}")
        }
    }

    fun getTrending(): Flow<List<Movie>> {
        return movieDao.getMoviesByCategory("trending")
            .map { entities -> entities.map { it.toUiModel() } }
    }

    suspend fun refreshTrending() {
        try {
            val response = apiService.getTrendingMovies()
            print("trending ${response.body()?.totalResults}")
            if (response.isSuccessful) {
                response.body()?.results?.let { dtos ->
                    val entities = dtos.map { it.toEntity("trending") }
                    movieDao.upsertAll(entities)
                }
            }
        } catch (e: Exception) { }
    }

    fun getPopular(): Flow<List<Movie>> {
        return movieDao.getMoviesByCategory("popular").map { entities -> entities.map { it.toUiModel()  }}
    }

    suspend fun refreshPopular() {
       try {
            val response = apiService.getPopularMovies()
            if (response.isSuccessful) {
                response.body()?.results?.let { dtos ->
                    val entities = dtos.map {
                        it.toEntity("popular")
                    }
                    movieDao.upsertAll(entities)
                }
            }
        }
        catch (e: Exception) {
            Log.d("MovieRepository", "Error refreshing popular: ${e.message}")
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return try {
            val response = apiService.searchMovies(query)
            if (response.isSuccessful) {
                response.body()?.results?.map { it.toUiModel() } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getBookmarks(): Flow<List<Movie>> {
        return movieDao.getBookmarkedMovies().map { entities ->
            entities.map { it.toUiModel() }
        }
    }

    suspend fun toggleBookmark(movieId: Int, isBookmarked: Boolean) {
        movieDao.updateBookmark(movieId, isBookmarked)
    }

    // Mappers
    private fun MovieDto.toEntity(category: String): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath ?: "",
            backdropPath = backdropPath ?: "",
            releaseDate = releaseDate ?: "",
            voteAverage = voteAverage,
            category = category
        )
    }

    private fun MovieDto.toUiModel(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath ?: "",
            backdropPath = backdropPath ?: "",
            rating = voteAverage,
            releaseYear = releaseDate?.take(4) ?: "",
            synopsis = overview
        )
    }

    private fun MovieEntity.toUiModel(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            rating = voteAverage,
            releaseYear = releaseDate.take(4),
            synopsis = overview,
            isBookmarked = isBookmarked
        )
    }
}
