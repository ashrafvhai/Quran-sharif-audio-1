package com.example.data.repository

import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.QuranDao
import com.example.data.local.RecentHistoryEntity
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

import kotlinx.coroutines.withContext

class QuranRepository(private val quranDao: QuranDao) {

    val allFavorites: Flow<List<FavoriteEntity>> = quranDao.getAllFavorites().flowOn(Dispatchers.IO)
    val recentHistory: Flow<List<RecentHistoryEntity>> = quranDao.getRecentHistory().flowOn(Dispatchers.IO)
    val completedDownloads: Flow<List<DownloadEntity>> = quranDao.getCompletedDownloads().flowOn(Dispatchers.IO)
    val allPlaylists: Flow<List<PlaylistEntity>> = quranDao.getAllPlaylists().flowOn(Dispatchers.IO)

    fun isFavorite(surahNumber: Int, reciterId: String): Flow<Boolean> {
        return quranDao.isFavorite(surahNumber, reciterId).flowOn(Dispatchers.IO)
    }

    suspend fun toggleFavorite(surahNumber: Int, reciterId: String, currentIsFav: Boolean) = withContext(Dispatchers.IO) {
        try {
            if (currentIsFav) {
                quranDao.deleteFavorite(surahNumber, reciterId)
            } else {
                quranDao.insertFavorite(FavoriteEntity(surahNumber, reciterId))
            }
        } catch (_: Exception) {}
    }

    suspend fun recordHistory(surahNumber: Int, reciterId: String, positionMs: Long, durationMs: Long) = withContext(Dispatchers.IO) {
        try {
            quranDao.insertRecentHistory(
                RecentHistoryEntity(
                    surahNumber = surahNumber,
                    reciterId = reciterId,
                    positionMs = positionMs,
                    durationMs = durationMs
                )
            )
        } catch (_: Exception) {}
    }

    fun startDownloadSimulation(scope: CoroutineScope, surahNumber: Int, reciterId: String) {
        scope.launch(Dispatchers.IO) {
            try {
                // Save initial downloading state
                quranDao.setDownload(DownloadEntity(surahNumber, reciterId, isDownloaded = false, progress = 0.2f))
                delay(600)
                quranDao.setDownload(DownloadEntity(surahNumber, reciterId, isDownloaded = false, progress = 0.65f))
                delay(600)
                quranDao.setDownload(DownloadEntity(surahNumber, reciterId, isDownloaded = true, progress = 1f))
            } catch (_: Exception) {}
        }
    }

    suspend fun removeDownload(surahNumber: Int, reciterId: String) = withContext(Dispatchers.IO) {
        try {
            quranDao.deleteDownload(surahNumber, reciterId)
        } catch (_: Exception) {}
    }

    suspend fun createPlaylist(title: String, description: String = "") = withContext(Dispatchers.IO) {
        try {
            val id = "pl_" + System.currentTimeMillis()
            quranDao.insertPlaylist(PlaylistEntity(id = id, title = title, description = description))
        } catch (_: Exception) {}
    }

    fun getSurah(number: Int): Surah {
        return QuranData.surahs.find { it.number == number } ?: QuranData.surahs.first()
    }

    fun getReciter(id: String): Reciter {
        return ReciterData.getById(id)
    }

    fun searchSurahs(query: String): List<Surah> {
        if (query.isBlank()) return QuranData.surahs
        val q = query.trim().lowercase()
        return QuranData.surahs.filter {
            it.nameEnglish.lowercase().contains(q) ||
            it.nameTranslation.lowercase().contains(q) ||
            it.nameArabic.contains(q) ||
            it.number.toString() == q
        }
    }

    fun searchReciters(query: String): List<Reciter> {
        if (query.isBlank()) return ReciterData.reciters
        val q = query.trim().lowercase()
        return ReciterData.reciters.filter {
            it.name.lowercase().contains(q) ||
            it.arabicName.contains(q) ||
            it.country.lowercase().contains(q) ||
            it.style.lowercase().contains(q)
        }
    }
}
