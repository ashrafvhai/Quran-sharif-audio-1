package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["surahNumber", "reciterId"])
data class FavoriteEntity(
    val surahNumber: Int,
    val reciterId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_history")
data class RecentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val reciterId: String,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlist_items")
data class PlaylistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: String,
    val surahNumber: Int,
    val reciterId: String
)

@Entity(tableName = "downloads", primaryKeys = ["surahNumber", "reciterId"])
data class DownloadEntity(
    val surahNumber: Int,
    val reciterId: String,
    val isDownloaded: Boolean = false,
    val progress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
