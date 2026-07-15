package com.personal.reels.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.reels.data.model.FeedItem
import com.personal.reels.data.repository.FeedRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: FeedRepository) : ViewModel() {

    val feed: StateFlow<List<FeedItem>> = repository.observeFeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addLocalVideos(uris: List<Pair<String, String>>) = viewModelScope.launch {
        repository.addLocalVideos(uris)
    }

    /** Returns false via callback if the pasted link isn't a valid YouTube URL. */
    fun addYouTubeLink(url: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        onResult(repository.addYouTubeLink(url))
    }

    fun toggleFavorite(item: FeedItem) = viewModelScope.launch {
        repository.toggleFavorite(item)
    }

    fun delete(item: FeedItem) = viewModelScope.launch {
        repository.delete(item)
    }
}
