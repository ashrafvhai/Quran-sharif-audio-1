package com.example.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SQLiteQuranDao(context: Context) : QuranDao {
    private val dbHelper = DatabaseHelper(context)

    private val _favoritesFlow = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    private val _recentHistoryFlow = MutableStateFlow<List<RecentHistoryEntity>>(emptyList())
    private val _downloadsFlow = MutableStateFlow<List<DownloadEntity>>(emptyList())
    private val _playlistsFlow = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    private val _playlistItemsFlow = MutableStateFlow<Map<String, List<PlaylistItemEntity>>>(emptyMap())

    init {
        // Initial load from SQLite database
        refreshAll()
    }

    private fun refreshAll() {
        try {
            _favoritesFlow.value = loadFavoritesFromDb()
            _recentHistoryFlow.value = loadRecentHistoryFromDb()
            _downloadsFlow.value = loadDownloadsFromDb()
            _playlistsFlow.value = loadPlaylistsFromDb()
            _playlistItemsFlow.value = loadAllPlaylistItemsFromDb()
        } catch (e: Exception) {
            android.util.Log.w("SQLiteQuranDao", "Failed initial refresh", e)
        }
    }

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> = _favoritesFlow

    override fun isFavorite(surahNumber: Int, reciterId: String): Flow<Boolean> {
        return _favoritesFlow.map { list ->
            list.any { it.surahNumber == surahNumber && it.reciterId == reciterId }
        }
    }

    override suspend fun insertFavorite(favorite: FavoriteEntity): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("surahNumber", favorite.surahNumber)
                put("reciterId", favorite.reciterId)
                put("addedAt", favorite.addedAt)
            }
            db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _favoritesFlow.value = loadFavoritesFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "insertFavorite error", e)
        }
    }

    override suspend fun deleteFavorite(surahNumber: Int, reciterId: String): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.delete("favorites", "surahNumber = ? AND reciterId = ?", arrayOf(surahNumber.toString(), reciterId))
            _favoritesFlow.value = loadFavoritesFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "deleteFavorite error", e)
        }
    }

    override fun getRecentHistory(): Flow<List<RecentHistoryEntity>> = _recentHistoryFlow

    override suspend fun insertRecentHistory(item: RecentHistoryEntity): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("surahNumber", item.surahNumber)
                put("reciterId", item.reciterId)
                put("playedAt", item.playedAt)
                put("completed", if (item.completed) 1 else 0)
            }
            db.insertWithOnConflict("recent_history", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _recentHistoryFlow.value = loadRecentHistoryFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "insertRecentHistory error", e)
        }
    }

    override fun getCompletedDownloads(): Flow<List<DownloadEntity>> {
        return _downloadsFlow.map { list -> list.filter { it.isDownloaded } }
    }

    override fun getDownload(surahNumber: Int, reciterId: String): Flow<DownloadEntity?> {
        return _downloadsFlow.map { list ->
            list.find { it.surahNumber == surahNumber && it.reciterId == reciterId }
        }
    }

    override suspend fun setDownload(download: DownloadEntity): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("surahNumber", download.surahNumber)
                put("reciterId", download.reciterId)
                put("isDownloaded", if (download.isDownloaded) 1 else 0)
                put("progress", download.progress)
                put("timestamp", download.timestamp)
            }
            db.insertWithOnConflict("downloads", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _downloadsFlow.value = loadDownloadsFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "setDownload error", e)
        }
    }

    override suspend fun deleteDownload(surahNumber: Int, reciterId: String): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.delete("downloads", "surahNumber = ? AND reciterId = ?", arrayOf(surahNumber.toString(), reciterId))
            _downloadsFlow.value = loadDownloadsFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "deleteDownload error", e)
        }
    }

    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> = _playlistsFlow

    override suspend fun insertPlaylist(playlist: PlaylistEntity): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("id", playlist.id)
                put("title", playlist.title)
                put("description", playlist.description)
                put("createdAt", playlist.createdAt)
            }
            db.insertWithOnConflict("playlists", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _playlistsFlow.value = loadPlaylistsFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "insertPlaylist error", e)
        }
    }

    override suspend fun deletePlaylist(id: String): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            db.delete("playlists", "id = ?", arrayOf(id))
            db.delete("playlist_items", "playlistId = ?", arrayOf(id))
            _playlistsFlow.value = loadPlaylistsFromDb()
            _playlistItemsFlow.value = loadAllPlaylistItemsFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "deletePlaylist error", e)
        }
    }

    override fun getPlaylistItems(playlistId: String): Flow<List<PlaylistItemEntity>> {
        return _playlistItemsFlow.map { map -> map[playlistId] ?: emptyList() }
    }

    override suspend fun insertPlaylistItem(item: PlaylistItemEntity): Unit = withContext(Dispatchers.IO) {
        try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("playlistId", item.playlistId)
                put("surahNumber", item.surahNumber)
                put("reciterId", item.reciterId)
                put("position", item.position)
            }
            db.insertWithOnConflict("playlist_items", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            _playlistItemsFlow.value = loadAllPlaylistItemsFromDb()
        } catch (e: Exception) {
            android.util.Log.e("SQLiteQuranDao", "insertPlaylistItem error", e)
        }
    }

    private fun loadFavoritesFromDb(): List<FavoriteEntity> {
        val list = mutableListOf<FavoriteEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT surahNumber, reciterId, addedAt FROM favorites ORDER BY addedAt DESC", null)
        cursor?.use {
            while (it.moveToNext()) {
                list.add(
                    FavoriteEntity(
                        surahNumber = it.getInt(0),
                        reciterId = it.getString(1),
                        addedAt = it.getLong(2)
                    )
                )
            }
        }
        return list
    }

    private fun loadRecentHistoryFromDb(): List<RecentHistoryEntity> {
        val list = mutableListOf<RecentHistoryEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT surahNumber, reciterId, playedAt, completed FROM recent_history ORDER BY playedAt DESC LIMIT 20", null)
        cursor?.use {
            while (it.moveToNext()) {
                list.add(
                    RecentHistoryEntity(
                        surahNumber = it.getInt(0),
                        reciterId = it.getString(1),
                        playedAt = it.getLong(2),
                        completed = it.getInt(3) == 1
                    )
                )
            }
        }
        return list
    }

    private fun loadDownloadsFromDb(): List<DownloadEntity> {
        val list = mutableListOf<DownloadEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT surahNumber, reciterId, isDownloaded, progress, timestamp FROM downloads ORDER BY timestamp DESC", null)
        cursor?.use {
            while (it.moveToNext()) {
                list.add(
                    DownloadEntity(
                        surahNumber = it.getInt(0),
                        reciterId = it.getString(1),
                        isDownloaded = it.getInt(2) == 1,
                        progress = it.getFloat(3),
                        timestamp = it.getLong(4)
                    )
                )
            }
        }
        return list
    }

    private fun loadPlaylistsFromDb(): List<PlaylistEntity> {
        val list = mutableListOf<PlaylistEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT id, title, description, createdAt FROM playlists ORDER BY createdAt DESC", null)
        cursor?.use {
            while (it.moveToNext()) {
                list.add(
                    PlaylistEntity(
                        id = it.getString(0),
                        title = it.getString(1),
                        description = it.getString(2) ?: "",
                        createdAt = it.getLong(3)
                    )
                )
            }
        }
        return list
    }

    private fun loadAllPlaylistItemsFromDb(): Map<String, List<PlaylistItemEntity>> {
        val map = mutableMapOf<String, MutableList<PlaylistItemEntity>>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT playlistId, surahNumber, reciterId, position FROM playlist_items ORDER BY position ASC", null)
        cursor?.use {
            while (it.moveToNext()) {
                val pid = it.getString(0)
                val item = PlaylistItemEntity(
                    playlistId = pid,
                    surahNumber = it.getInt(1),
                    reciterId = it.getString(2),
                    position = it.getInt(3)
                )
                map.getOrPut(pid) { mutableListOf() }.add(item)
            }
        }
        return map
    }

    private class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "easy_quranify_database", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS favorites (
                    surahNumber INTEGER NOT NULL,
                    reciterId TEXT NOT NULL,
                    addedAt INTEGER NOT NULL,
                    PRIMARY KEY(surahNumber, reciterId)
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS downloads (
                    surahNumber INTEGER NOT NULL,
                    reciterId TEXT NOT NULL,
                    isDownloaded INTEGER NOT NULL,
                    progress REAL NOT NULL,
                    timestamp INTEGER NOT NULL,
                    PRIMARY KEY(surahNumber, reciterId)
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS playlists (
                    id TEXT PRIMARY KEY NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS playlist_items (
                    playlistId TEXT NOT NULL,
                    surahNumber INTEGER NOT NULL,
                    reciterId TEXT NOT NULL,
                    position INTEGER NOT NULL,
                    PRIMARY KEY(playlistId, surahNumber, reciterId)
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS recent_history (
                    surahNumber INTEGER NOT NULL,
                    reciterId TEXT NOT NULL,
                    playedAt INTEGER NOT NULL,
                    completed INTEGER NOT NULL,
                    PRIMARY KEY(surahNumber, reciterId)
                )
            """.trimIndent())
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // No migrations yet
        }
    }
}
