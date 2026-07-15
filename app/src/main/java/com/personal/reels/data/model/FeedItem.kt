package com.personal.reels.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SourceType is the single flag the whole app branches on to decide
 * "play with ExoPlayer" vs "play with the YouTube IFrame WebView".
 * Keeping it a 2-value enum (instead of a generic "provider" string)
 * keeps the `when` blocks exhaustive and cheap.
 */
enum class SourceType { LOCAL, YOUTUBE }

/**
 * Single unified row for both local files and YouTube links.
 */
@Entity(tableName = "feed_items")
data class FeedItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourceType: SourceType,

    // For LOCAL: a content:// URI string from the Photo Picker.
    // For YOUTUBE: the raw URL the user pasted.
    val sourceUri: String,

    // For YOUTUBE this is resolved from the URL (see YouTubeUrlParser).
    // For LOCAL this stays null in the MVP — see README for the deferred
    // thumbnail-extraction plan.
    val thumbnailUri: String? = null,

    val durationMs: Long? = null,
    val dateAddedEpochMs: Long = System.currentTimeMillis(),
    val category: String? = null,
    val isFavorite: Boolean = false
)

/**
 * Pulls the 11-character video ID out of any common YouTube URL shape.
 * This is the ONLY "parsing" this app does to a YouTube link — it never
 * touches YouTube's private APIs or attempts to resolve a stream URL.
 */
object YouTubeUrlParser {
    private val patterns = listOf(
        Regex("""youtu\.be/([A-Za-z0-9_-]{11})"""),
        Regex("""youtube\.com/watch\?v=([A-Za-z0-9_-]{11})"""),
        Regex("""youtube\.com/shorts/([A-Za-z0-9_-]{11})"""),
        Regex("""youtube\.com/embed/([A-Za-z0-9_-]{11})""")
    )

    fun extractVideoId(url: String): String? =
        patterns.firstNotNullOfOrNull { it.find(url)?.groupValues?.get(1) }

    fun isValidYouTubeUrl(url: String): Boolean = extractVideoId(url) != null

    fun thumbnailFor(videoId: String): String =
        "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
}
