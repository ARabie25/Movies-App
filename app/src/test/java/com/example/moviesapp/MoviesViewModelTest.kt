package com.example.moviesapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.usecases.GetMoviesUseCase
import com.example.moviesapp.domain.usecases.ToggleFavoriteUseCase
import com.example.moviesapp.helper.utils.NetworkUtil
import com.example.moviesapp.ui.home.viewModel.MoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MoviesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MoviesViewModel
    private val getMoviesUseCase: GetMoviesUseCase = mock()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private val networkUtil: NetworkUtil = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MoviesViewModel(getMoviesUseCase, toggleFavoriteUseCase, networkUtil)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchMovies should emit movies when online`() = runTest {
        // Arrange
        val movies = PagingData.from(listOf(Movie(1, "Movie 1", "", "2023", false)))
        whenever(networkUtil.isOnline()).thenReturn(true)
        whenever(getMoviesUseCase()).thenReturn(flowOf(movies))

        // Act
        viewModel.processIntent(MoviesIntent.FetchMovies)
        advanceUntilIdle()

        // Assert
        viewModel.state.collect { state ->
            assert(state.isLoading == false)
            assert(state.movies == movies)
            assert(state.isOffline == false)
            assert(state.error == null)
        }
    }

    @Test
    fun `fetchMovies should emit error when offline`() = runTest {
        // Arrange
        whenever(networkUtil.isOnline()).thenReturn(false)
        whenever(getMoviesUseCase()).thenThrow(RuntimeException("Network error"))

        // Act
        viewModel.processIntent(MoviesIntent.FetchMovies)
        advanceUntilIdle()

        // Assert
        viewModel.state.collect { state ->
            assert(state.isLoading == false)
            assert(state.isOffline == true)
            assert(state.error == "Network error")
        }
    }

    @Test
    fun `switchLayout should toggle isGridLayout`() = runTest {
        // Act
        viewModel.processIntent(MoviesIntent.SwitchLayout)
        advanceUntilIdle()

        // Assert
        viewModel.state.collect { state ->
            assert(state.isGridLayout == true)
        }

        // Act again to toggle back
        viewModel.processIntent(MoviesIntent.SwitchLayout)
        advanceUntilIdle()

        // Assert
        viewModel.state.collect { state ->
            assert(state.isGridLayout == false)
        }
    }

    @Test
    fun `toggleFavorite should call use case`() = runTest {
        // Arrange
        val movieId = 1
        val isFavorite = true

        // Act
        viewModel.processIntent(MoviesIntent.ToggleFavorite(movieId, isFavorite))
        advanceUntilIdle()

        // Assert
        verify(toggleFavoriteUseCase).invoke(movieId, isFavorite)
    }
}
