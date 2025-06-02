package com.example.moviesapp.data.remote.model.dto

data class MovieDetailsDto(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val release_date: String,
    val overview: String,
    val genres: List<GenreDto>,
    val runtime: Int?
)

data class GenreDto(
    val name: String
)