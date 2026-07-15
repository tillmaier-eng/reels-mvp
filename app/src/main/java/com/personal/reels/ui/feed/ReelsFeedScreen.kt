package com.personal.reels.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.personal.reels.data.model.FeedItem
import com.personal.reels.data.model.SourceType
import com.personal.reels.data.model.YouTubeUrlParser
import com.personal.reels.player.LocalVideoPlayer
import com.personal.reels.player.YouTubePlayer

/**
 * The whole "TikTok-style" experience is this one VerticalPager: only the
 * settled page (currentPage, not just the nearest one mid-drag) is ever
 * marked `isActive = true`, which is what drives auto-play/auto-pause and
 * guarantees exactly one decoder (ExoPlayer) or one WebView (YouTube) is
 * ever doing real work at a time.
 */
@Composable
fun ReelsFeedScreen(
    items: List<FeedItem>,
    onToggleFavorite: (FeedItem) -> Unit,
    onDelete: (FeedItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        EmptyFeedState(modifier)
        return
    }

    val pagerState = rememberPagerState(pageCount = { items.size })

    // settledPage (not currentPage) avoids double-playback flicker while
    // the user's finger is still mid-swipe between two pages.
    val settledPage by remember {
        derivedStateOf { pagerState.settledPage }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        val item = items[page]
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            FeedItemPlayer(item = item, isActive = page == settledPage)
            FeedItemOverlay(
                item = item,
                onToggleFavorite = { onToggleFavorite(item) },
                onDelete = { onDelete(item) },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun FeedItemPlayer(item: FeedItem, isActive: Boolean) {
    when (item.sourceType) {
        SourceType.LOCAL -> LocalVideoPlayer(
            uri = item.sourceUri,
            isActive = isActive,
            modifier = Modifier.fillMaxSize()
        )
        SourceType.YOUTUBE -> {
            val videoId = remember(item.sourceUri) { YouTubeUrlParser.extractVideoId(item.sourceUri) }
            if (videoId != null) {
                YouTubePlayer(
                    videoId = videoId,
                    isActive = isActive,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/** Favorite + delete controls, stacked in the bottom-right corner like a reels action rail. */
@Composable
private fun FeedItemOverlay(
    item: FeedItem,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(PaddingValues(end = 12.dp, bottom = 32.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = onToggleFavorite) {
            Icon(
                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (item.isFavorite) Color(0xFFFF4081) else Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun EmptyFeedState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Text(
            "কোনো ভিডিও নেই — নিচের ডান কোণের + বাটনে যোগ করুন",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(32.dp)
        )
    }
}
