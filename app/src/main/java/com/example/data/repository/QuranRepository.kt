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
import java.util.Locale
import java.util.UUID

class QuranRepository(private val quranDao: QuranDao) {
    val allFavorites: Flow<List<FavoriteEntity>> = quranDao.getAllFavorites().flowOn(Dispatchers.IO)
    val recentHistory: Flow<List<RecentHistoryEntity>> = quranDao.getRecentHistory().flowOn(Dispatchers.IO)
    val completedDownloads: Flow<List<DownloadEntity>> = quranDao.getCompletedDownloads().flowOn(Dispatchers.IO)
    val allPlaylists: Flow<List<PlaylistEntity>> = quranDao.getAllPlaylists().flowOn(Dispatchers.IO)

    fun isFavorite(surahNumber: Int, reciterId: String): Flow<Boolean> {
        return quranDao.isFavorite(surahNumber, reciterId).flowOn(Dispatchers.IO)
    }

    suspend fun toggleFavorite(surahNumber: Int, reciterId: String, currentIsFav: Boolean) {
        withContext(Dispatchers.IO) {
            if (currentIsFav) {
                quranDao.deleteFavorite(surahNumber, reciterId)
            } else {
                quranDao.insertFavorite(FavoriteEntity(surahNumber, reciterId))
            }
        }
    }

    suspend fun recordHistory(surahNumber: Int, reciterId: String, positionMs: Long, durationMs: Long) {
        withContext(Dispatchers.IO) {
            quranDao.insertRecentHistory(
                RecentHistoryEntity(
                    surahNumber = surahNumber,
                    reciterId = reciterId,
                    playedAt = System.currentTimeMillis(),
                    completed = positionMs >= durationMs * 0.9f
                )
            )
        }
    }

    fun startDownloadSimulation(scope: CoroutineScope, surahNumber: Int, reciterId: String) {
        scope.launch(Dispatchers.IO) {
            for (p in 1..10) {
                quranDao.setDownload(
                    DownloadEntity(
                        surahNumber = surahNumber,
                        reciterId = reciterId,
                        isDownloaded = p == 10,
                        progress = p / 10f
                    )
                )
                delay(200)
            }
        }
    }

    suspend fun removeDownload(surahNumber: Int, reciterId: String) {
        withContext(Dispatchers.IO) {
            quranDao.deleteDownload(surahNumber, reciterId)
        }
    }

    suspend fun createPlaylist(title: String, description: String = "") {
        withContext(Dispatchers.IO) {
            quranDao.insertPlaylist(
                PlaylistEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    description = description
                )
            )
        }
    }

    fun getSurah(number: Int): Surah {
        return QuranData.getByNumber(number)
    }

    fun getReciter(id: String): Reciter {
        return ReciterData.getById(id)
    }

    fun searchSurahs(query: String): List<Surah> {
        if (query.isBlank()) return QuranData.surahs
        val q = query.trim().lowercase(Locale.ROOT)
        return QuranData.surahs.filter { surah ->
            surah.number.toString() == q ||
                    surah.nameEnglish.lowercase(Locale.ROOT).contains(q) ||
                    surah.nameTranslation.lowercase(Locale.ROOT).contains(q) ||
                    surah.nameArabic.contains(q)
        }
    }

    fun searchReciters(query: String): List<Reciter> {
        if (query.isBlank()) return ReciterData.reciters
        val q = query.trim().lowercase(Locale.ROOT)
        return ReciterData.reciters.filter { reciter ->
            reciter.name.lowercase(Locale.ROOT).contains(q) ||
                    reciter.arabicName.contains(q) ||
                    reciter.country.lowercase(Locale.ROOT).contains(q) ||
                    reciter.style.lowercase(Locale.ROOT).contains(q)
        }
    }
}
