package com.upendra.movieplus.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upendra.movieplus.ui.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookmarksViewModel : ViewModel() {

    private val _bookmarks = MutableStateFlow<List<Movie>>(emptyList())
    val bookmarks: StateFlow<List<Movie>> = _bookmarks.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            // Simulating Room reactive flow
            _bookmarks.value = listOf(
                Movie(1, "The Dark Knight", "/poster1.jpg", "/backdrop1.jpg", 9.0),
                Movie(2, "Inception", "/poster2.jpg", "/backdrop2.jpg", 8.8)
            )
        }
    }

    fun removeBookmark(movie: Movie) {
        _bookmarks.value = _bookmarks.value.filter { it.id != movie.id }
    }
}
