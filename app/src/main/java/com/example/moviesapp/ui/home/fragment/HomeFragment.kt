package com.example.moviesapp.ui.home.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesapp.R
import com.example.moviesapp.databinding.FragmentHomeBinding
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.helper.base.BaseFragment
import com.example.moviesapp.ui.home.adapter.MoviesAdapter
import com.example.moviesapp.ui.home.uiState.MoviesIntent
import com.example.moviesapp.ui.home.viewModel.MoviesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment :BaseFragment<FragmentHomeBinding>() {

        private val viewModel: MoviesViewModel by viewModel()
        private lateinit var adapter: MoviesAdapter
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)    }

    override fun setupUI() {
            adapter = MoviesAdapter(
                onMovieClick = { movieId ->
                    viewModel.processIntent(MoviesIntent.NavigateToDetails(movieId))
                    navController.navigate(
                        HomeFragmentDirections.actionMoviesListFragmentToMovieDetailsFragment(movieId)
                    )
                },
                onFavoriteClick = { movieId, isFavorite ->
                    viewModel.processIntent(MoviesIntent.ToggleFavorite(movieId, isFavorite))
                }
            )

            binding.moviesRecyclerView.adapter = adapter

            binding.toggleLayoutButton.setOnClickListener {
                viewModel.processIntent(MoviesIntent.SwitchLayout)
            }
        binding.moviesRecyclerView.let { state ->
            binding.moviesRecyclerView.layoutManager?.onRestoreInstanceState(state as Parcelable?)
        }

        }

        override fun setupObservers() {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.state.collectLatest { state ->
                    binding.loadingProgress.isVisible = state.isLoading
                    binding.moviesRecyclerView.layoutManager = if (state.isGridLayout) {
                        binding.toggleLayoutButton.setImageResource(R.drawable.ic_list)
                        GridLayoutManager(requireContext(), 2)
                    } else {
                        binding.toggleLayoutButton.setImageResource(R.drawable.ic_grid)
                        LinearLayoutManager(requireContext())
                    }
                    adapter.submitData(state.movies)

                    if (state.isOffline) {
                        showError(getString(R.string.internet_connectivity))
                    }
                    state.error?.let {
                        showError(it)
                    }
                }
            }
        }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.moviesRecyclerView.layoutManager?.onSaveInstanceState()?.let { state ->
            outState.putParcelable("recycler_state", state)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getParcelable<Parcelable>("recycler_state")?.let { state ->
            binding.moviesRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }
    }
    }