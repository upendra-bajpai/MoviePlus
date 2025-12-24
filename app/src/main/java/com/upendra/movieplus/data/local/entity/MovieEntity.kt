package com.upendra.movieplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val category: String, // "trending", "now_playing","popular"
    val isBookmarked: Boolean = false,
    val runtime: Int? = null,
    val tagline: String? = null,
    val budget: Long? = null,
    val genres: String? = null, // Comma separated
    val timestamp: Long = System.currentTimeMillis()
)
