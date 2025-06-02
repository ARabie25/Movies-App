package com.example.moviesapp.ui.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.usecases.GetMoviesUseCase
import com.example.moviesapp.domain.usecases.ToggleFavoriteUseCase
import com.example.moviesapp.helper.utils.NetworkUtil
import com.example.moviesapp.ui.home.uiState.MovieState
import com.example.moviesapp.ui.home.uiState.MoviesIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoviesViewModel(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkUtil: NetworkUtil
) : ViewModel() {

    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    init {
        processIntent(MoviesIntent.FetchMovies)
    }

    fun processIntent(intent: MoviesIntent) {
        when (intent) {
            is MoviesIntent.FetchMovies -> fetchMovies()
            is MoviesIntent.ToggleFavorite -> toggleFavorite(intent.movieId, intent.isFavorite)
            is MoviesIntent.SwitchLayout -> switchLayout()
            is MoviesIntent.NavigateToDetails -> {}
        }
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isOffline = !networkUtil.isOnline()) }
            try {
                getMoviesUseCase().cachedIn(viewModelScope).collect { pagingData ->
                    _state.update { it.copy(movies = pagingData, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message, isOffline = !networkUtil.isOnline()) }
            }
        }
    }

    private fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movieId, isFavorite)
        }
    }

    private fun switchLayout() {
        _state.update { it.copy(isGridLayout = !it.isGridLayout) }
    }
}