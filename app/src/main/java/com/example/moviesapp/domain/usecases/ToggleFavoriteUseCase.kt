package com.example.moviesapp.domain.usecases

import com.example.moviesapp.domain.repository.MovieRepository

class ToggleFavoriteUseCase (private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int, isFavorite: Boolean) {
        repository.toggleFavorite(movieId, isFavorite)
    }
}