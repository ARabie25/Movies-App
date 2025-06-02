package com.example.moviesapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.moviesapp.data.local.room.dao.MovieDao
import com.example.moviesapp.data.remote.api.MovieApi
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.repository.MovieRepository
import com.example.moviesapp.helper.utils.NetworkUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val apiKey: String,
    private val networkUtil: NetworkUtil
) : MovieRepository {

    override fun getMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { MoviesPagingSource(api, dao, apiKey,networkUtil) }
        ).flow.map { pagingData ->
            pagingData.map { movie ->
                // Check favorite status from Room
                val isFavorite = dao.getFavoriteMovies().first().any { it.id == movie.id }
                movie.copy(isFavorite = isFavorite)
            }
        }
        }


    override suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        dao.updateFavorite(movieId, isFavorite)
    }

    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> {
        return if (networkUtil.isOnline()) {
            try {
                val response = api.getMovieDetails(movieId, apiKey)
                Result.success(response.toDomainModel())
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Offline: Try to get from cache
            val cachedMovie = dao.getCachedMovies(0).first().find { it.id == movieId }
            if (cachedMovie != null) {
                Result.success(
                    MovieDetails(
                        id = cachedMovie.id,
                        title = cachedMovie.title,
                        posterPath = cachedMovie.posterPath,
                        releaseDate = cachedMovie.releaseDate,
                        overview = "", // Limited details in cache
                        genres = emptyList(),
                        runtime = null,
                        isFavorite = cachedMovie.isFavorite
                    )
                )
            } else {
                Result.failure(Exception("No cached data available"))
            }
        }
    }
}