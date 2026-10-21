package com.example.data.local

data class FavoriteEntity(
    val surahNumber: Int,
    val reciterId: String,
    val addedAt: Long = System.currentTimeMillis()
)

data class DownloadEntity(
    val surahNumber: Int,
    val reciterId: String,
    val isDownloaded: Boolean = false,
    val progress: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

data class PlaylistEntity(
    val id: String,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class PlaylistItemEntity(
    val playlistId: String,
    val surahNumber: Int,
    val reciterId: String,
    val position: Int = 0
)

data class RecentHistoryEntity(
    val surahNumber: Int,
    val reciterId: String,
    val playedAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false
)
