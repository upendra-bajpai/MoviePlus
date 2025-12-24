package com.upendra.movieplus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upendra.movieplus.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(movies: List<MovieEntity>)

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY timestamp ASC")
    fun getMoviesByCategory(category: String): Flow<List<MovieEntity>>

    @Query("UPDATE movies SET isBookmarked = :isBookmarked WHERE id = :movieId")
    suspend fun updateBookmark(movieId: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM movies WHERE isBookmarked = 1")
    fun getBookmarkedMovies(): Flow<List<MovieEntity>>

    @Query("DELETE FROM movies WHERE category = :category AND isBookmarked = 0")
    suspend fun clearCategory(category: String)

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?
}
