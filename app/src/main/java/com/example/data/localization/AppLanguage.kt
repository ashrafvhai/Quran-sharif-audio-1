package com.example.data.localization

enum class AppLanguage(
    val code: String,
    val title: String,
    val nativeName: String,
    val flag: String,
    val description: String
) {
    ENGLISH(
        code = "en",
        title = "English",
        nativeName = "English",
        flag = "🇬🇧",
        description = "Continue with English interface"
    ),
    BENGALI(
        code = "bn",
        title = "Bengali",
        nativeName = "বাংলা",
        flag = "🇧🇩",
        description = "বাংলা ভাষায় অ্যাপটি ব্যবহার করুন"
    ),
    ARABIC(
        code = "ar",
        title = "Arabic",
        nativeName = "العربية",
        flag = "🇸🇦",
        description = "متابعة الواجهة باللغة العربية"
    )
}
