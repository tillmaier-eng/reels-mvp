package com.personal.reels.player

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Plays a YouTube video via Google's official IFrame Player API
 * (https://developers.google.com/youtube/iframe_api_reference), loaded
 * inside a WebView. This is the ONLY playback path used for YouTube
 * content in the app:
 *  - No video/audio stream URL is ever resolved or downloaded.
 *  - Playback, ads, and analytics are handled entirely by YouTube's own
 *    player.js running inside the WebView sandbox — same as embedding a
 *    YouTube video in a webpage.
 *  - This keeps the app compliant with YouTube's Terms of Service.
 *
 * Cost/benefit note: a WebView-based embed is heavier per-item than a
 * native ExoPlayer surface (extra JS engine + HTML parsing), which is why
 * `isActive` gating matters even more here than for local video — an
 * inactive YouTube WebView is paused via a JS bridge call rather than
 * destroyed on every scroll, and only fully released on dispose, to avoid
 * re-paying WebView init cost on every swipe.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayer(
    videoId: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val html = remember(videoId) {
        """
        <html><body style="margin:0;padding:0;background:#000;">
        <div id="player"></div>
        <script src="https://www.youtube.com/iframe_api"></script>
        <script>
          var player;
          function onYouTubeIframeAPIReady() {
            player = new YT.Player('player', {
              height: '100%',
              width: '100%',
              videoId: '$videoId',
              playerVars: { 'playsinline': 1, 'controls': 0, 'rel': 0, 'modestbranding': 1 },
              events: {
                'onReady': function(e) { if (window.__active) { e.target.playVideo(); } }
              }
            });
          }
          window.setActive = function(active) {
            window.__active = active;
            if (player && player.playVideo) {
              if (active) { player.playVideo(); } else { player.pauseVideo(); }
            }
          };
        </script>
        </body></html>
        """.trimIndent()
    }

    var view: WebView? by remember { mutableStateOf(null) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                loadDataWithBaseURL(
                    "https://www.youtube.com", html, "text/html", "utf-8", null
                )
                view = this
            }
        },
        update = { wv ->
            wv.evaluateJavascript("window.setActive && window.setActive($isActive);", null)
        }
    )

    DisposableEffect(videoId) {
        onDispose { view?.destroy() }
    }
}
