package com.example.data.localization

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LanguageManager {
    private const val PREFS_NAME = "easy_quranify_language_prefs"
    private const val KEY_LANGUAGE = "selected_app_language"
    private const val KEY_FIRST_LAUNCH_COMPLETED = "first_launch_language_selected"

    private val _currentLanguage = MutableStateFlow(AppLanguage.BENGALI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow(true)
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    private var prefs: SharedPreferences? = null

    fun initialize(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        val hasSelectedLanguage = sp.getBoolean(KEY_FIRST_LAUNCH_COMPLETED, false)
        _isFirstLaunch.value = !hasSelectedLanguage

        val savedCode = sp.getString(KEY_LANGUAGE, null)
        val lang = AppLanguage.entries.find { it.code == savedCode } ?: AppLanguage.BENGALI
        _currentLanguage.value = lang
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        _currentLanguage.value = language
        val sp = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun completeFirstLaunch(context: Context, language: AppLanguage) {
        _currentLanguage.value = language
        _isFirstLaunch.value = false
        val sp = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_LANGUAGE, language.code)
            .putBoolean(KEY_FIRST_LAUNCH_COMPLETED, true)
            .apply()
    }
}
