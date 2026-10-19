package com.example.data.model

data class Reciter(
    val id: String,
    val name: String,
    val arabicName: String,
    val country: String,
    val subtitle: String,
    val bio: String,
    val serverUrlPattern: String,
    val fallbackServerUrlPattern: String,
    val listenerCount: String,
    val isFeatured: Boolean = false,
    val style: String,
    val avatarUrl: String,
    val localDrawableRes: Int? = null
) {
    fun getAudioUrl(surahNumber: Int): String = String.format(serverUrlPattern, surahNumber)
    fun getFallbackAudioUrl(surahNumber: Int): String = String.format(fallbackServerUrlPattern, surahNumber)
}
