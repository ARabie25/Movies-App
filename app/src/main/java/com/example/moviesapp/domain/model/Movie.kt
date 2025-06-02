package com.example.moviesapp.domain.model

data class Movie(val id: Int,
                 val title: String,
                 val posterPath: String?,
                 val releaseDate: String,
                 val isFavorite: Boolean = false)
