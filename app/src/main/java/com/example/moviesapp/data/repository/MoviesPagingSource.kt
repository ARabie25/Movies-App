package com.example.moviesapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviesapp.data.local.room.dao.MovieDao
import com.example.moviesapp.data.local.room.entity.MovieEntity
import com.example.moviesapp.data.remote.api.MovieApi
import com.example.moviesapp.data.remote.model.dto.MovieDetailsDto
import com.example.moviesapp.data.remote.model.dto.MovieDto
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.helper.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MoviesPagingSource(
    private val api: MovieApi,
    private val dao: MovieDao,
    private val apiKey: String,
    private val networkUtil: NetworkUtil
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            if (networkUtil.isOnline()) {
                // Online: Fetch from API and cache
                val response = api.getMovies(page, apiKey)
                val movies = response.results.map { it.toDomainModel() }

                withContext(Dispatchers.IO) {
                    // Clear old cache (older than 24 hours)
                    val cacheThreshold = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
                    dao.clearOldCache(cacheThreshold)
                    // Cache new movies
                    dao.insertMovies(movies.map { it.toEntity() })
                }

                LoadResult.Page(
                    data = movies,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (movies.isEmpty()) null else page + 1
                )
            } else {
                // Offline: Load from cache
                val cacheThreshold = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)
                val cachedMovies = withContext(Dispatchers.IO) {
                    dao.getCachedMovies(cacheThreshold).first().map { it.toDomainModel() }
                }
                LoadResult.Page(
                    data = cachedMovies,
                    prevKey = null,
                    nextKey = null // No pagination in offline mode
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

// Extension functions for mapping
fun MovieDto.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = poster_path,
        releaseDate = release_date
    )
}

fun MovieDetailsDto.toDomainModel(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        posterPath = poster_path,
        releaseDate = release_date,
        overview = overview,
        genres = genres.map { it.name },
        runtime = runtime
    )
}

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        isFavorite = isFavorite
    )
}

fun MovieEntity.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate,
        isFavorite = isFavorite
    )
}