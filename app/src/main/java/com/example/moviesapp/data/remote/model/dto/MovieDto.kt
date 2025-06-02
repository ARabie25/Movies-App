package com.example.moviesapp.data.remote.model.dto

data class MovieDto(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String
)
