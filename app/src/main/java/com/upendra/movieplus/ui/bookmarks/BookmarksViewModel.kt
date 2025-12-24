package com.upendra.movieplus.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.upendra.movieplus.data.repository.MovieRepository

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Movie>>(emptyList())
    val bookmarks: StateFlow<List<Movie>> = _bookmarks.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            repository.getBookmarks().collect {
                _bookmarks.value = it
            }
        }
    }

    fun removeBookmark(movie: Movie) {
        viewModelScope.launch {
            repository.toggleBookmark(movie.id, false)
        }
    }
}
