package com.example.moviesapp.ui.movieDetails.fragment
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.core.view.isVisible
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.navArgs
    import com.bumptech.glide.Glide
    import com.example.moviesapp.R
    import com.example.moviesapp.databinding.FragmentHomeBinding
    import com.example.moviesapp.databinding.FragmentMovieDetailsBinding
    import com.example.moviesapp.helper.base.BaseFragment
    import com.example.moviesapp.ui.movieDetails.uiState.MovieDetailsIntent
    import com.example.moviesapp.ui.movieDetails.viewModel.MovieDetailsViewModel
    import com.google.android.material.snackbar.Snackbar
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch
    import org.koin.androidx.viewmodel.ext.android.viewModel

    class MovieDetailsFragment : BaseFragment<FragmentMovieDetailsBinding>() {

        private val viewModel: MovieDetailsViewModel by viewModel()
        private val args: MovieDetailsFragmentArgs by navArgs()
        override fun inflateBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
        ): FragmentMovieDetailsBinding {
            return FragmentMovieDetailsBinding.inflate(inflater, container, false)    }


        override fun setupUI() {

            viewModel.processIntent(MovieDetailsIntent.LoadDetails(args.movieID))
            binding.favoriteButton.setOnClickListener {
                val isFavorite = binding.favoriteButton.tag as? Boolean ?: false
                viewModel.processIntent(MovieDetailsIntent.ToggleFavorite(args.movieID, !isFavorite))
            }
            binding.materialToolbar.setNavigationOnClickListener {
                navController.navigateUp()
            }
        }

        override fun setupObservers() {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.state.collectLatest { state ->
                    binding.loadingProgress.isVisible = state.isLoading

                    state.movieDetails?.let { details ->
                        binding.materialToolbar.title = details.title
                        Glide.with(binding.moviePoster)
                            .load("https://image.tmdb.org/t/p/w500${details.posterPath}")
                            .into(binding.moviePoster)
                        binding.movieTitle.text = details.title
                        binding.releaseDate.text = details.releaseDate
                        binding.genres.text = details.genres.joinToString(", ")
                        binding.runtime.text = details.runtime?.let { "$it min" } ?: "N/A"
                        binding.overview.text = details.overview
                        binding.favoriteButton.setImageResource(
                            if (details.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                        )
                        binding.favoriteButton.tag = details.isFavorite
                    }

                    if (state.isOffline) {
                        showError(getString(R.string.internet_connectivity))
                    }
                    state.error?.let {
                        showError(it)
                    }
                }
            }
        }
    }