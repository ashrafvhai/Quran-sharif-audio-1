package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["surahNumber", "reciterId"])
data class FavoriteEntity(
    val surahNumber: Int,
    val reciterId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads", primaryKeys = ["surahNumber", "reciterId"])
data class DownloadEntity(
    val surahNumber: Int,
    val reciterId: String,
    val isDownloaded: Boolean = false,
    val progress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_items", primaryKeys = ["playlistId", "surahNumber", "reciterId"])
data class PlaylistItemEntity(
    val playlistId: String,
    val surahNumber: Int,
    val reciterId: String,
    val position: Int = 0
)

@Entity(tableName = "recent_history", primaryKeys = ["surahNumber", "reciterId"])
data class RecentHistoryEntity(
    val surahNumber: Int,
    val reciterId: String,
    val playedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)
