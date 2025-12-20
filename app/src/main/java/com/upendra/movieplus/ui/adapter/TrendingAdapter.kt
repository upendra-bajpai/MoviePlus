package com.upendra.movieplus.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.upendra.movieplus.databinding.ItemMovieFeaturedBinding
import com.upendra.movieplus.ui.model.Movie

class TrendingAdapter(
    private val movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val binding = ItemMovieFeaturedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class TrendingViewHolder(private val binding: ItemMovieFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            Glide.with(binding.ivFeatured)
                .load("https://image.tmdb.org/t/p/original${movie.backdropPath}")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivFeatured)

            binding.tvTitle.text = movie.title
            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }
}
