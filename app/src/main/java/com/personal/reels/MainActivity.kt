package com.personal.reels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personal.reels.ui.add.AddVideoScreen
import com.personal.reels.ui.feed.FeedViewModel
import com.personal.reels.ui.feed.FeedViewModelFactory
import com.personal.reels.ui.feed.ReelsFeedScreen
import com.personal.reels.ui.theme.ReelsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as ReelsApp).container.feedRepository

        setContent {
            ReelsTheme {
                val viewModel: FeedViewModel = viewModel(factory = FeedViewModelFactory(repository))
                var showAddScreen by remember { mutableStateOf(false) }
                val items by viewModel.feed.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (showAddScreen) {
                        AddVideoScreen(
                            viewModel = viewModel,
                            onDone = { showAddScreen = false }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ReelsFeedScreen(
                                items = items,
                                onToggleFavorite = viewModel::toggleFavorite,
                                onDelete = viewModel::delete
                            )
                            FloatingActionButton(
                                onClick = { showAddScreen = true },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(24.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add video")
                            }
                        }
                    }
                }
            }
        }
    }
}
