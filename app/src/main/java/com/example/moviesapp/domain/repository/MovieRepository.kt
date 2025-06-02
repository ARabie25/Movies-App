package com.example.moviesapp.domain.repository


import androidx.paging.PagingData
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<PagingData<Movie>>
    suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean)
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>
}