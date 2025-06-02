package com.example.moviesapp.ui.home.uiState

import androidx.paging.PagingData
import com.example.moviesapp.domain.model.Movie

data class MovieState(
    val isLoading: Boolean = false,
    val movies: PagingData<Movie> = PagingData.empty(),
    val isGridLayout: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false
)

sealed class MoviesIntent {
    object FetchMovies : MoviesIntent()
    data class ToggleFavorite(val movieId: Int, val isFavorite: Boolean) : MoviesIntent()
    object SwitchLayout : MoviesIntent()
    data class NavigateToDetails(val movieId: Int) : MoviesIntent()
}