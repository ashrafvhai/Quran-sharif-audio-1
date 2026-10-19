package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val versesCount: Int,
    val revelationType: String,
    val juzNumber: Int,
    val sampleAyahArabic: String = "",
    val sampleAyahEnglish: String = ""
)
