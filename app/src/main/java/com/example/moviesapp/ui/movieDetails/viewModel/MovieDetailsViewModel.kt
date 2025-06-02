package com.example.moviesapp.ui.movieDetails.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.usecases.GetMovieDetailsUseCase
import com.example.moviesapp.domain.usecases.ToggleFavoriteUseCase
import com.example.moviesapp.helper.utils.NetworkUtil
import com.example.moviesapp.ui.movieDetails.uiState.MovieDetailsIntent
import com.example.moviesapp.ui.movieDetails.uiState.MovieDetailsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val networkUtil: NetworkUtil
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsState())
    val state: StateFlow<MovieDetailsState> = _state.asStateFlow()

    fun processIntent(intent: MovieDetailsIntent) {
        when (intent) {
            is MovieDetailsIntent.LoadDetails -> loadDetails(intent.movieId)
            is MovieDetailsIntent.ToggleFavorite -> toggleFavorite(intent.movieId, intent.isFavorite)
        }
    }

    private fun loadDetails(movieId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isOffline = !networkUtil.isOnline()) }
            getMovieDetailsUseCase(movieId).onSuccess { details ->
                _state.update { it.copy(isLoading = false, movieDetails = details) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message, isOffline = !networkUtil.isOnline()) }
            }
        }
    }

    private fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movieId, isFavorite)
            _state.update { it.copy(movieDetails = it.movieDetails?.copy(isFavorite = isFavorite)) }
        }
    }
}