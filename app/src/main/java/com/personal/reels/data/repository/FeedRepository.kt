package com.personal.reels.data.repository

import com.personal.reels.data.local.FeedDao
import com.personal.reels.data.model.FeedItem
import com.personal.reels.data.model.SourceType
import com.personal.reels.data.model.YouTubeUrlParser
import kotlinx.coroutines.flow.Flow

/**
 * Thin repository — no caching layer beyond Room itself, and no network
 * calls beyond what the YouTube WebView player performs on its own.
 * Keeping this dumb-and-small is a deliberate performance choice: fewer
 * moving parts, faster startup, smaller APK.
 */
class FeedRepository(private val dao: FeedDao) {

    fun observeFeed(): Flow<List<FeedItem>> = dao.observeAll()
    fun observeFavorites(): Flow<List<FeedItem>> = dao.observeFavorites()

    /** uris: list of (displayName, contentUriString) from the Photo Picker. */
    suspend fun addLocalVideos(uris: List<Pair<String, String>>) {
        val items = uris.map { (name, uri) ->
            FeedItem(title = name, sourceType = SourceType.LOCAL, sourceUri = uri)
        }
        dao.insertAll(items)
    }

    /** Returns false if the URL isn't a recognizable YouTube link. */
    suspend fun addYouTubeLink(url: String, title: String? = null): Boolean {
        val videoId = YouTubeUrlParser.extractVideoId(url) ?: return false
        dao.insert(
            FeedItem(
                title = title ?: "YouTube video",
                sourceType = SourceType.YOUTUBE,
                sourceUri = url,
                thumbnailUri = YouTubeUrlParser.thumbnailFor(videoId)
            )
        )
        return true
    }

    suspend fun toggleFavorite(item: FeedItem) = dao.setFavorite(item.id, !item.isFavorite)
    suspend fun delete(item: FeedItem) = dao.delete(item)
}
