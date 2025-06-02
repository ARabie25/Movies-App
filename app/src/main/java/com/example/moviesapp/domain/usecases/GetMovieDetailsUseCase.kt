package com.example.moviesapp.domain.usecases

import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.repository.MovieRepository

class GetMovieDetailsUseCase (private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): Result<MovieDetails> {
        return repository.getMovieDetails(movieId)
    }
}