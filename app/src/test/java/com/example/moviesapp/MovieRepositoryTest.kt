package com.example.moviesapp

import androidx.paging.PagingData
import com.example.moviesapp.data.api.TmdbApi
import com.example.moviesapp.data.db.MovieDao
import com.example.moviesapp.data.db.MovieEntity
import com.example.moviesapp.data.local.room.dao.MovieDao
import com.example.moviesapp.data.local.room.entity.MovieEntity
import com.example.moviesapp.data.remote.api.MovieApi
import com.example.moviesapp.data.repository.MovieRepositoryImpl
import com.example.moviesapp.domain.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    private lateinit var repository: MovieRepositoryImpl
    private val tmdbApi: MovieApi = mock()
    private val movieDao: MovieDao = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = MovieRepositoryImpl(tmdbApi, movieDao, mock(), mock())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getMovies should return PagingData`() = runTest {
        // Arrange
        val movies = listOf(Movie(1, "Movie 1", "/poster.jpg", "2023", false))
        whenever(tmdbApi.getPopularMovies(1)).thenReturn(mock()) // Mock API response
        whenever(movieDao.getFavoriteMovies()).thenReturn(flowOf(emptyList()))

        // Act
        val result = repository.getMovies()
        advanceUntilIdle()

        // Assert
        result.collect { pagingData ->
            assert(pagingData is PagingData<Movie>)
        }
    }

    @Test
    fun `toggleFavorite should insert movie when favorite`() = runTest {
        // Arrange
        val movie = Movie(1, "Movie 1", "/poster.jpg", "2023", true)
        whenever(movieDao.getMovieById(1)).thenReturn(null)

        // Act
        repository.toggleFavorite(movie)
        advanceUntilIdle()

        // Assert
        verify(movieDao).insert(MovieEntity(1, "Movie 1", "/poster.jpg", "2023"))
    }

    @Test
    fun `toggleFavorite should delete movie when not favorite`() = runTest {
        // Arrange
        val movie = Movie(1, "Movie 1", "/poster.jpg", "2023", false)
        whenever(movieDao.getMovieById(1)).thenReturn(MovieEntity(1, "Movie 1", "/poster.jpg", "2023"))

        // Act
        repository.toggleFavorite(movie)
        advanceUntilIdle()

        // Assert
        verify(movieDao).delete(MovieEntity(1, "Movie 1", "/poster.jpg", "2023"))
    }
}
