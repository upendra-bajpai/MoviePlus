package com.upendra.movieplus.data.repository

import android.util.Log
import com.upendra.movieplus.data.local.MovieDao
import com.upendra.movieplus.data.local.entity.MovieEntity
import com.upendra.movieplus.data.remote.MovieApiService
import com.upendra.movieplus.data.remote.dto.MovieDetailsDto
import com.upendra.movieplus.data.remote.dto.MovieDto
import com.upendra.movieplus.utils.Resource
import com.upendra.movieplus.ui.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    fun getMovieDetails(movieId: Int): Flow<Resource<Movie>> = flow {
        emit(Resource.Loading)

        // Check local DB first
        val cachedMovie = movieDao.getMovieById(movieId)
        if (cachedMovie != null && cachedMovie.runtime != null) {
            emit(Resource.Success(cachedMovie.toUiModel()))
        }

        // Fetch from network
        try {
            val response = apiService.getMovieDetails(movieId)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val updatedEntity = dto.toEntity(
                        category = cachedMovie?.category ?: "unknown",
                        isBookmarked = cachedMovie?.isBookmarked ?: false
                    )
                    movieDao.upsertAll(listOf(updatedEntity))
                    emit(Resource.Success(updatedEntity.toUiModel()))
                }
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            if (cachedMovie != null) {
                emit(Resource.Success(cachedMovie.toUiModel()))
            } else {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
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

    private fun MovieDetailsDto.toEntity(category: String, isBookmarked: Boolean): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath ?: "",
            backdropPath = backdropPath ?: "",
            releaseDate = releaseDate ?: "",
            voteAverage = voteAverage,
            category = category,
            isBookmarked = isBookmarked,
            runtime = runtime,
            tagline = tagline,
            budget = budget,
            genres = genres?.joinToString { it.name }
        )
    }

    private fun MovieEntity.toUiModel(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            rating = voteAverage,
            duration = runtime?.let { "$it min" } ?: "",
            releaseYear = releaseDate.take(4),
            synopsis = overview,
            genres = genres?.split(", ") ?: emptyList(),
            tagline = tagline ?: "",
            isBookmarked = isBookmarked
        )
    }
}
