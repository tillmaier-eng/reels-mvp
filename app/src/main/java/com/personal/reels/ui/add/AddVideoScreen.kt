package com.personal.reels.ui.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.personal.reels.ui.feed.FeedViewModel

/**
 * Two ways in, one feed out:
 *  1) System Photo Picker for local videos — chosen over a raw storage
 *     document tree / READ_MEDIA_VIDEO permission because it needs no
 *     runtime permission dialog and only grants access to the exact
 *     files picked.
 *  2) A plain text field for pasting a YouTube URL, validated client-side
 *     via YouTubeUrlParser before it's written to Room.
 */
@Composable
fun AddVideoScreen(
    viewModel: FeedViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var youtubeUrl by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val pickVideosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            // Persist read access across app restarts for these specific URIs.
            uris.forEach { uri ->
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            val named = uris.map { uri ->
                (queryDisplayName(context, uri) ?: "Local video") to uri.toString()
            }
            viewModel.addLocalVideos(named)
            onDone()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("ভিডিও যোগ করুন", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            pickVideosLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }) {
            Text("লোকাল ভিডিও বাছাই করুন")
        }

        HorizontalDivider()

        OutlinedTextField(
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it; errorText = null },
            label = { Text("YouTube লিংক পেস্ট করুন") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                viewModel.addYouTubeLink(youtubeUrl) { ok ->
                    if (ok) { youtubeUrl = ""; onDone() }
                    else errorText = "এটি একটি সঠিক YouTube লিংক নয়"
                }
            },
            enabled = youtubeUrl.isNotBlank()
        ) {
            Text("যোগ করুন")
        }
        errorText?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.weight(1f))
        TextButton(onClick = onDone) { Text("ফিডে ফিরে যান") }
    }
}

private fun queryDisplayName(context: android.content.Context, uri: android.net.Uri): String? {
    val projection = arrayOf(android.provider.OpenableColumns.DISPLAY_NAME)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && cursor.moveToFirst()) return cursor.getString(idx)
    }
    return null
}
