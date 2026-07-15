package com.personal.reels.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personal.reels.data.model.FeedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    // Ordered by date so newest additions surface first in the feed.
    @Query("SELECT * FROM feed_items ORDER BY dateAddedEpochMs DESC")
    fun observeAll(): Flow<List<FeedItem>>

    @Query("SELECT * FROM feed_items WHERE isFavorite = 1 ORDER BY dateAddedEpochMs DESC")
    fun observeFavorites(): Flow<List<FeedItem>>

    @Query("SELECT * FROM feed_items WHERE category = :category ORDER BY dateAddedEpochMs DESC")
    fun observeByCategory(category: String): Flow<List<FeedItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: FeedItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FeedItem>)

    @Update
    suspend fun update(item: FeedItem)

    @Delete
    suspend fun delete(item: FeedItem)

    @Query("UPDATE feed_items SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)
}
