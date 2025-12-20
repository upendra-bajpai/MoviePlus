package com.upendra.movieplus.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.upendra.movieplus.databinding.ItemMoviePosterBinding
import com.upendra.movieplus.ui.model.Movie

class MovieAdapter(private val onMovieClick: (Movie) -> Unit) :
    ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMoviePosterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemMoviePosterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            Glide.with(binding.moviePoster)
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.moviePoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
