package com.example.moviesapp.domain.usecases

import androidx.paging.PagingData
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Flow<PagingData<Movie>> {
        return repository.getMovies()
    }
}