package com.example.data.repository

import android.content.Context
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.UUID

class QuranRepository(
    private val quranDao: QuranDao,
    private val context: Context? = null
) {
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

    /**
     * Real online downloader: Downloads the high-definition studio audio directly
     * from the reciter's CDN into app internal storage for 100% offline playback.
     */
    fun downloadSurah(scope: CoroutineScope, surahNumber: Int, reciter: Reciter) {
        scope.launch(Dispatchers.IO) {
            try {
                quranDao.setDownload(
                    DownloadEntity(
                        surahNumber = surahNumber,
                        reciterId = reciter.id,
                        isDownloaded = false,
                        progress = 0.05f
                    )
                )

                val audioDir = if (context != null) File(context.filesDir, "audio") else null
                if (audioDir != null && !audioDir.exists()) {
                    audioDir.mkdirs()
                }

                val primaryUrl = reciter.getAudioUrl(surahNumber)
                var connection = (URL(primaryUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = true
                }
                connection.connect()

                if (connection.responseCode in 300..399) {
                    val redirectUrl = connection.getHeaderField("Location")
                    if (!redirectUrl.isNullOrEmpty()) {
                        connection.disconnect()
                        connection = (URL(redirectUrl).openConnection() as HttpURLConnection).apply {
                            connectTimeout = 15000
                            readTimeout = 30000
                        }
                        connection.connect()
                    }
                }

                if (connection.responseCode !in 200..299) {
                    // Try fallback
                    connection.disconnect()
                    val fallbackUrl = reciter.getFallbackAudioUrl(surahNumber)
                    connection = (URL(fallbackUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 15000
                        readTimeout = 30000
                        instanceFollowRedirects = true
                    }
                    connection.connect()
                }

                val totalLength = connection.contentLengthLong
                val targetFile = if (audioDir != null) File(audioDir, "surah_${surahNumber}_${reciter.id}.mp3") else null
                val tempFile = if (audioDir != null) File(audioDir, "surah_${surahNumber}_${reciter.id}.tmp") else null

                if (tempFile != null && targetFile != null) {
                    connection.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            val buffer = ByteArray(16384)
                            var read: Int
                            var totalRead = 0L
                            var lastReportTime = System.currentTimeMillis()

                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                totalRead += read

                                val now = System.currentTimeMillis()
                                if (now - lastReportTime > 300 && totalLength > 0) {
                                    val prog = (totalRead.toFloat() / totalLength.toFloat()).coerceIn(0.05f, 0.95f)
                                    quranDao.setDownload(
                                        DownloadEntity(
                                            surahNumber = surahNumber,
                                            reciterId = reciter.id,
                                            isDownloaded = false,
                                            progress = prog
                                        )
                                    )
                                    lastReportTime = now
                                }
                            }
                        }
                    }

                    if (targetFile.exists()) targetFile.delete()
                    tempFile.renameTo(targetFile)

                    quranDao.setDownload(
                        DownloadEntity(
                            surahNumber = surahNumber,
                            reciterId = reciter.id,
                            isDownloaded = true,
                            progress = 1.0f
                        )
                    )
                }
                connection.disconnect()
            } catch (e: Exception) {
                android.util.Log.e("QuranRepository", "Error downloading surah $surahNumber", e)
                quranDao.deleteDownload(surahNumber, reciter.id)
            }
        }
    }

    suspend fun removeDownload(surahNumber: Int, reciterId: String) {
        withContext(Dispatchers.IO) {
            try {
                if (context != null) {
                    val file = File(context.filesDir, "audio/surah_${surahNumber}_${reciterId}.mp3")
                    if (file.exists()) file.delete()
                }
                quranDao.deleteDownload(surahNumber, reciterId)
            } catch (e: Exception) {
                android.util.Log.e("QuranRepository", "Error removing download", e)
            }
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
