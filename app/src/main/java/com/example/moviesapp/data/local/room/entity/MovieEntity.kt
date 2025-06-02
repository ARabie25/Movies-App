package com.example.moviesapp.data.local.room.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)
