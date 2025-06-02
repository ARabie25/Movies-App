package com.example.moviesapp.domain.model

data class MovieDetails(val id: Int,
                        val title: String,
                        val posterPath: String?,
                        val releaseDate: String,
                        val overview: String,
                        val genres: List<String>,
                        val runtime: Int?,
                        val isFavorite: Boolean = false)
