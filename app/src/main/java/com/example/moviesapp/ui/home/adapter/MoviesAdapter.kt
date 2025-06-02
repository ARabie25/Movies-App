package com.example.moviesapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviesapp.R
import com.example.moviesapp.databinding.ItemMovieBinding
import com.example.moviesapp.domain.model.Movie

class MoviesAdapter(
    private val onMovieClick: (Int) -> Unit,
    private val onFavoriteClick: (Int, Boolean) -> Unit
) : PagingDataAdapter<Movie, MoviesAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            Glide.with(binding.moviePoster)
                .load("https://image.tmdb.org/t/p/w200${movie.posterPath}")
                .into(binding.moviePoster)
            binding.movieTitle.text = movie.title
            binding.releaseDate.text = movie.releaseDate
            binding.favoriteButton.setImageResource(
                if (movie.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            )
            binding.favoriteButton.tag = movie.isFavorite
            binding.root.setOnClickListener { onMovieClick(movie.id) }
            binding.favoriteButton.setOnClickListener {
                val isFavorite = binding.favoriteButton.tag as? Boolean ?: false
                onFavoriteClick(movie.id, !isFavorite)
            }
        }
    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}