package com.example.data.localization

import com.example.data.model.Surah

object LocalizedStrings {
    // App header
    fun appName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Easy Quranify"
        AppLanguage.BENGALI -> "ইজি কুরআনিফাই"
        AppLanguage.ARABIC -> "إيزي قرآني فاي"
    }

    fun appSlogan(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Holy Quran Recitation & Serene Ambience"
        AppLanguage.BENGALI -> "পবিত্র কুরআন তিলাওয়াত ও শান্তিময় পরিবেশ"
        AppLanguage.ARABIC -> "تلاوة القرآن الكريم والسكينة الروحية"
    }

    // Tabs
    fun tabHome(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Home"
        AppLanguage.BENGALI -> "হোম"
        AppLanguage.ARABIC -> "الرئيسية"
    }

    fun tabReciters(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Reciters"
        AppLanguage.BENGALI -> "ক্বারীগণ"
        AppLanguage.ARABIC -> "القراء"
    }

    fun tabPlaylists(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Playlists"
        AppLanguage.BENGALI -> "প্লেলিস্ট"
        AppLanguage.ARABIC -> "قوائم التشغيل"
    }

    fun tabSettings(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.BENGALI -> "সেটিংস"
        AppLanguage.ARABIC -> "الإعدادات"
    }

    // Home Section
    fun featuredQaris(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Featured Qaris"
        AppLanguage.BENGALI -> "প্রখ্যাত ক্বারীগণ"
        AppLanguage.ARABIC -> "مشاهير القراء"
    }

    fun swipeQarisTip(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Swipe sideways to choose your Qari"
        AppLanguage.BENGALI -> "সোয়াইপ করে পছন্দের ক্বারীর তিলাওয়াত শুনুন"
        AppLanguage.ARABIC -> "اسحب لاختيار قارئك المفضل"
    }

    fun recitersCount(count: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "$count Reciters"
        AppLanguage.BENGALI -> "$count জন ক্বারী"
        AppLanguage.ARABIC -> "$count قراء"
    }

    fun searchSurah(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Search Surah by name or number..."
        AppLanguage.BENGALI -> "সূরা নাম বা নম্বর দিয়ে খুঁজুন..."
        AppLanguage.ARABIC -> "ابحث عن السورة بالاسم أو الرقم..."
    }

    fun activeBadge(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Active"
        AppLanguage.BENGALI -> "চলমান"
        AppLanguage.ARABIC -> "نشط"
    }

    fun playingBadge(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Playing"
        AppLanguage.BENGALI -> "বাজছে"
        AppLanguage.ARABIC -> "تشغيل"
    }

    fun queueOrder(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Queue Order"
        AppLanguage.BENGALI -> "প্লেলিস্ট ক্রম"
        AppLanguage.ARABIC -> "ترتيب التشغيل"
    }

    // Reciters Screen
    fun recitersHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "World-Renowned Qaris"
        AppLanguage.BENGALI -> "প্রখ্যাত ক্বারীগণ"
        AppLanguage.ARABIC -> "مشاهير القراء في العالم"
    }

    fun recitersSubHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "10 Iconic Qaris with Studio HD Audio"
        AppLanguage.BENGALI -> "বিশ্ববিখ্যাত ১০ জন ক্বারীর সুললিত কণ্ঠ"
        AppLanguage.ARABIC -> "١٠ قراء مشهورين بتسجيلات استوديو نقية"
    }

    fun searchReciter(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Search reciter by name..."
        AppLanguage.BENGALI -> "ক্বারীর নাম দিয়ে খুঁজুন..."
        AppLanguage.ARABIC -> "ابحث عن القارئ بالاسم..."
    }

    // FullScreen Player
    fun playingSurahHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "PLAYING SURAH"
        AppLanguage.BENGALI -> "চলমান সূরা"
        AppLanguage.ARABIC -> "السورة الجارية"
    }

    fun hdQariBadge(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "HD Qari"
        AppLanguage.BENGALI -> "এইচডি ক্বারী"
        AppLanguage.ARABIC -> "قارئ استوديو"
    }

    fun selectReciterTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Select Reciter"
        AppLanguage.BENGALI -> "ক্বারী নির্বাচন"
        AppLanguage.ARABIC -> "اختر القارئ"
    }

    fun selectReciterSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "10 World-Renowned Qaris"
        AppLanguage.BENGALI -> "সুললিত কণ্ঠের বিশিষ্ট ১০ জন বিশ্বখ্যাত ক্বারী"
        AppLanguage.ARABIC -> "نخبة من كبار قراء العالم الإسلامي"
    }

    fun speed(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Speed"
        AppLanguage.BENGALI -> "গতি"
        AppLanguage.ARABIC -> "السرعة"
    }

    fun volume(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Volume"
        AppLanguage.BENGALI -> "ভলিউম"
        AppLanguage.ARABIC -> "الصوت"
    }

    fun ambient(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Ambient"
        AppLanguage.BENGALI -> "অ্যাম্বিয়েন্ট"
        AppLanguage.ARABIC -> "أصوات هادئة"
    }

    fun scenic(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Scenic"
        AppLanguage.BENGALI -> "প্রকৃতি"
        AppLanguage.ARABIC -> "مشاهد طبيعية"
    }

    fun mushaf(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Mushaf"
        AppLanguage.BENGALI -> "মুসহাফ"
        AppLanguage.ARABIC -> "المصحف"
    }

    fun queue(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Queue"
        AppLanguage.BENGALI -> "কিউ"
        AppLanguage.ARABIC -> "قائمة التشغيل"
    }

    // Playlists Screen
    fun playlistsHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Favorites & Downloads"
        AppLanguage.BENGALI -> "প্রিয় সূরা ও ডাউনলোড"
        AppLanguage.ARABIC -> "المفضلة والتنزيلات"
    }

    fun favoritesTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Favorites"
        AppLanguage.BENGALI -> "প্রিয় তালিকা"
        AppLanguage.ARABIC -> "المفضلة"
    }

    fun downloadsTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Downloads"
        AppLanguage.BENGALI -> "ডাউনলোড"
        AppLanguage.ARABIC -> "التنزيلات"
    }

    fun noFavorites(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "No favorite Surahs added yet"
        AppLanguage.BENGALI -> "এখনও কোন প্রিয় সূরা যুক্ত করা হয়নি"
        AppLanguage.ARABIC -> "لم تتم إضافة أي سور إلى المفضلة بعد"
    }

    fun noDownloads(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "No downloaded Surahs yet"
        AppLanguage.BENGALI -> "কোন অফলাইন সূরা ডাউনলোড নেই"
        AppLanguage.ARABIC -> "لا توجد سور منزلة حتى الآن"
    }

    // Settings Screen
    fun settingsHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Settings & Customization"
        AppLanguage.BENGALI -> "সেটিংস ও কাস্টমাইজেশন"
        AppLanguage.ARABIC -> "الإعدادات والتخصيص"
    }

    fun settingsSubHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Language, background playback & visual themes"
        AppLanguage.BENGALI -> "ভাষা, ব্যাকগ্রাউন্ড অডিও এবং থিম পছন্দসমূহ"
        AppLanguage.ARABIC -> "اللغة، التشغيل في الخلفية، والمظاهر"
    }

    fun languageTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "App Language (ভাষা নির্বাচন)"
        AppLanguage.BENGALI -> "অ্যাপের ভাষা (App Language)"
        AppLanguage.ARABIC -> "لغة التطبيق (App Language)"
    }

    fun languageSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "English • বাংলা • العربية"
        AppLanguage.BENGALI -> "ইংরেজি • বাংলা • আরবি"
        AppLanguage.ARABIC -> "الإنجليزية • البنغالية • العربية"
    }

    fun backgroundAudioTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Background Audio & Controls"
        AppLanguage.BENGALI -> "ব্যাকগ্রাউন্ড অডিও ও কন্ট্রোল"
        AppLanguage.ARABIC -> "التشغيل في الخلفية وأدوات التحكم"
    }

    fun backgroundAudioDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Audio continues playing when app is minimized or screen is locked. Manage playback from top notification shade."
        AppLanguage.BENGALI -> "অ্যাপ মিনিমাইজ বা স্ক্রিন বন্ধ করলেও ব্যাকগ্রাউন্ডে অবিরাম তিলাওয়াত চলবে এবং নোটিফিকেশন বার থেকে সহজে নিয়ন্ত্রণ করা যাবে।"
        AppLanguage.ARABIC -> "يستمر تشغيل التلاوة في الخلفية وعند إغلاق الشاشة مع أزرار تحكم كاملة في شريط الإشعارات."
    }

    fun resetQueueTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Reset Queue Sequence (1-114)"
        AppLanguage.BENGALI -> "প্লেলিস্টের ক্রম রিসেট করুন (১-১১৪)"
        AppLanguage.ARABIC -> "إعادة ضبط تسلسل القائمة (١-١١٤)"
    }

    fun resetQueueDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Reset playlist back to standard Quran Surah order"
        AppLanguage.BENGALI -> "প্লেলিস্টের ক্রমকে মূল সূরা ক্রম অনুসারে সাজাও"
        AppLanguage.ARABIC -> "إعادة ترتيب قائمة التشغيل حسب تسلسل سور المصحف الشريف"
    }

    fun visualThemesTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Visual Glass Themes"
        AppLanguage.BENGALI -> "ভিজুয়াল গ্লাস থিম"
        AppLanguage.ARABIC -> "السمات الزجاجية الجمالية"
    }

    // First Launch Language Selection Screen
    fun selectLanguagePrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Choose Your Language"
        AppLanguage.BENGALI -> "আপনার ভাষা নির্বাচন করুন"
        AppLanguage.ARABIC -> "اختر لغتك المفضلة"
    }

    fun welcomeText(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Welcome to Easy Quranify. Please select your preferred language to get started."
        AppLanguage.BENGALI -> "ইজি কুরআনিফাই-এ আপনাকে স্বাগতম। অ্যাপটি শুরু করতে আপনার পছন্দের ভাষাটি বেছে নিন।"
        AppLanguage.ARABIC -> "مرحبًا بك في تطبيق إيزي قرآني فاي. يرجى اختيار لغتك المفضلة للمتابعة."
    }

    fun quranArabicNotice(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Holy Quran verses and Arabic calligraphy will remain in authentic Arabic text."
        AppLanguage.BENGALI -> "পবিত্র কুরআনের মূল আরবি আয়াত ও নামসমূহ অবিকল আরবী হরফেই সংরক্ষিত থাকবে।"
        AppLanguage.ARABIC -> "ستظل آيات القرآن الكريم والنصوص الأصلية باللغة العربية المباركة."
    }

    fun confirmAndProceed(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Confirm & Continue"
        AppLanguage.BENGALI -> "নিশ্চিত করুন ও শুরু করুন"
        AppLanguage.ARABIC -> "تأكيد والبدء"
    }

    // Helper for Surah Subtitle details
    fun getSurahSubtitle(surah: Surah, lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> {
            "${surah.versesCount} Ayahs • ${surah.revelationType} • Juz ${surah.juzNumber}"
        }
        AppLanguage.BENGALI -> {
            val type = if (surah.revelationType.equals("Meccan", ignoreCase = true)) "মক্কী" else "মাদানী"
            "${surah.versesCount} আয়াত • $type • পারা ${surah.juzNumber}"
        }
        AppLanguage.ARABIC -> {
            val type = if (surah.revelationType.equals("Meccan", ignoreCase = true)) "مكية" else "مدنية"
            "${surah.versesCount} آيات • $type • الجزء ${surah.juzNumber}"
        }
    }
}
