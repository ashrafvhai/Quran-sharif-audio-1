package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {
    // Favorites
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE surahNumber = :surahNumber AND reciterId = :reciterId)")
    fun isFavorite(surahNumber: Int, reciterId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE surahNumber = :surahNumber AND reciterId = :reciterId")
    suspend fun deleteFavorite(surahNumber: Int, reciterId: String)

    // Recent History
    @Query("SELECT * FROM recent_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentHistory(): Flow<List<RecentHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentHistory(item: RecentHistoryEntity)

    // Downloads
    @Query("SELECT * FROM downloads WHERE isDownloaded = 1 ORDER BY timestamp DESC")
    fun getCompletedDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE surahNumber = :surahNumber AND reciterId = :reciterId")
    fun getDownload(surahNumber: Int, reciterId: String): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE surahNumber = :surahNumber AND reciterId = :reciterId")
    suspend fun deleteDownload(surahNumber: Int, reciterId: String)

    // Playlists
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: String)

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId")
    fun getPlaylistItems(playlistId: String): Flow<List<PlaylistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistItem(item: PlaylistItemEntity)
}
