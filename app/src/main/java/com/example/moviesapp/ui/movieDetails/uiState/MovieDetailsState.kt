package com.example.moviesapp.ui.movieDetails.uiState

import com.example.moviesapp.domain.model.MovieDetails

data class MovieDetailsState(
    val isLoading: Boolean = false,
    val movieDetails: MovieDetails? = null,
    val error: String? = null,
    val isOffline: Boolean = false
)

sealed class MovieDetailsIntent {
    data class LoadDetails(val movieId: Int) : MovieDetailsIntent()
    data class ToggleFavorite(val movieId: Int, val isFavorite: Boolean) : MovieDetailsIntent()
}