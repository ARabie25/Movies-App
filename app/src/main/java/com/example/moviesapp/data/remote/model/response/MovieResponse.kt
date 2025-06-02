package com.example.moviesapp.data.remote.model.response

import com.example.moviesapp.data.remote.model.dto.MovieDto


data class MovieResponse(
    val results: List<MovieDto>,
    val page: Int,
    val total_pages: Int
)

