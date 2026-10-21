package com.example.data.local

import kotlinx.coroutines.flow.Flow

interface QuranDao {
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    fun isFavorite(surahNumber: Int, reciterId: String): Flow<Boolean>
    suspend fun insertFavorite(favorite: FavoriteEntity)
    suspend fun deleteFavorite(surahNumber: Int, reciterId: String)

    fun getRecentHistory(): Flow<List<RecentHistoryEntity>>
    suspend fun insertRecentHistory(item: RecentHistoryEntity)

    fun getCompletedDownloads(): Flow<List<DownloadEntity>>
    fun getDownload(surahNumber: Int, reciterId: String): Flow<DownloadEntity?>
    suspend fun setDownload(download: DownloadEntity)
    suspend fun deleteDownload(surahNumber: Int, reciterId: String)

    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun insertPlaylist(playlist: PlaylistEntity)
    suspend fun deletePlaylist(id: String)

    fun getPlaylistItems(playlistId: String): Flow<List<PlaylistItemEntity>>
    suspend fun insertPlaylistItem(item: PlaylistItemEntity)
}
