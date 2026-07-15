package com.personal.reels.di

import android.content.Context
import com.personal.reels.data.local.AppDatabase
import com.personal.reels.data.repository.FeedRepository

/**
 * Manual dependency injection on purpose: Hilt/Dagger add annotation
 * processing time to every build and a non-trivial method-count chunk for
 * an app that only ever needs a single repository + DAO. A hand-written
 * container is faster to build, easier to reason about, and keeps the
 * APK smaller.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.get(context)
    val feedRepository: FeedRepository = FeedRepository(database.feedDao())
}
