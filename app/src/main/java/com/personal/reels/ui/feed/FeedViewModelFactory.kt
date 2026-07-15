package com.personal.reels.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.personal.reels.data.repository.FeedRepository

class FeedViewModelFactory(private val repository: FeedRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FeedViewModel::class.java))
        return FeedViewModel(repository) as T
    }
}
