package com.example.data.model

import com.example.R

object ReciterData {
    val reciters = listOf(
        Reciter(
            id = "afs",
            name = "Sheikh Mishary Rashid Alafasy",
            arabicName = "مشاري بن راشد العفاسي",
            country = "Kuwait 🇰🇼",
            subtitle = "Kuwait • Renowned Qari",
            bio = "Sheikh Mishary bin Rashid Alafasy is a world-renowned Kuwaiti Qari, Imam, and Islamic scholar. Celebrated globally for his crystalline tone, soothing melodic cadence, and emotional recitation of the Holy Quran.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/mishary-alafasy/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/%03d.mp3",
            listenerCount = "9.8M listeners",
            isFeatured = true,
            style = "Harmonious & Melodic",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_mishary
        ),
        Reciter(
            id = "sudais",
            name = "Sheikh Abdur-Rahman As-Sudais",
            arabicName = "عبد الرحمن السديس",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Imam of Masjid al-Haram, Makkah",
            bio = "Sheikh Dr. Abdur-Rahman Ibn Abdul Aziz As-Sudais is the General President for the Affairs of the Two Holy Mosques and legendary chief Imam of the Grand Mosque (Masjid al-Haram) in Makkah.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/abdulrahman-sudais/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/abdurrahmaan_as-sudays/%03d.mp3",
            listenerCount = "9.5M listeners",
            isFeatured = true,
            style = "Iconic Grand Mosque Imam",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_sudais
        ),
        Reciter(
            id = "shuraim",
            name = "Sheikh Saud Ash-Shuraim",
            arabicName = "سعود الشريم",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Imam of Masjid al-Haram, Makkah",
            bio = "Sheikh Dr. Saud Ibn Ibrahim Ibn Muhammad Ash-Shuraim served for decades as the beloved Imam and Khateeb of the Grand Mosque in Makkah. Famed for crisp Tajweed articulation.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/saud-shuraim/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/sa3ood_al-shuraym/%03d.mp3",
            listenerCount = "8.1M listeners",
            isFeatured = false,
            style = "Majestic & Crisp Tajweed",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_shuraim
        ),
        Reciter(
            id = "maher",
            name = "Sheikh Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Imam of Masjid al-Haram, Makkah",
            bio = "Sheikh Dr. Maher bin Hamad Al-Muaiqly is an Imam and Khateeb of the Grand Mosque in Makkah. Celebrated universally for his crystal-clear, peaceful, and deeply tranquil recitation.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/maher-muaiqly/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://server12.mp3quran.net/maher/%03d.mp3",
            listenerCount = "8.9M listeners",
            isFeatured = true,
            style = "Tranquil & Heartfelt",
            avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_maher
        ),
        Reciter(
            id = "basit",
            name = "Sheikh Abdul Basit Abdul Samad",
            arabicName = "عبد الباسط عبد الصمد",
            country = "Egypt 🇪🇬",
            subtitle = "Egypt • The Golden Voice",
            bio = "Sheikh Abdul Basit Muhammad Abdus-Samad is widely revered as the 'Golden Voice' of the Islamic world and one of the most iconic Qaris in history.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/abdulbasit-abdulsamad/r3/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/abdul_basit_murattal/%03d.mp3",
            listenerCount = "9.9M listeners",
            isFeatured = true,
            style = "The Golden Voice • Master Qari",
            avatarUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_abdulbasit
        ),
        Reciter(
            id = "husary",
            name = "Sheikh Mahmoud Khalil Al-Husary",
            arabicName = "محمود خليل الحصري",
            country = "Egypt 🇪🇬",
            subtitle = "Egypt • Master Teacher of Tajweed",
            bio = "Sheikh Mahmoud Khalil Al-Husary was a legendary Egyptian Qari widely acclaimed as the 'Shaykh of Qaris'. The first to record the complete Qur'an in the Murattal style.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/mahmoud-husary/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/mahmood_khaleel_al-husaree/%03d.mp3",
            listenerCount = "6.7M listeners",
            isFeatured = false,
            style = "The Master Teacher of Tajweed",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_husary
        ),
        Reciter(
            id = "ghamdi",
            name = "Sheikh Saad Al-Ghamdi",
            arabicName = "سعد الغامدي",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Saudi Arabia • Celebrated Qari",
            bio = "Sheikh Saad Al-Ghamdi is a distinguished Saudi scholar and celebrated Qari, famed for his smooth and steady Tajweed recitation with meditative calm.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/saad-ghamdi/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://server7.mp3quran.net/s_gmd/%03d.mp3",
            listenerCount = "7.2M listeners",
            isFeatured = false,
            style = "Smooth & Meditative Calm",
            avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_ghamdi
        ),
        Reciter(
            id = "dossary",
            name = "Sheikh Yasser Al-Dossari",
            arabicName = "ياسر الدوسري",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Imam of Masjid al-Haram, Makkah",
            bio = "Sheikh Dr. Yasser bin Rashid Al-Dossari is an Imam of the Grand Mosque in Makkah. He is celebrated worldwide for his deeply poignant, emotional, and resonant recitation.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/yasser-dosari/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/yasser_ad-dussary/%03d.mp3",
            listenerCount = "9.1M listeners",
            isFeatured = true,
            style = "Emotional & Resonant",
            avatarUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_dossary
        ),
        Reciter(
            id = "ajmi",
            name = "Sheikh Ahmad Al-Ajmy",
            arabicName = "أحمد بن علي العجمي",
            country = "Saudi Arabia 🇸🇦",
            subtitle = "Saudi Arabia • Khobar Qari",
            bio = "Sheikh Ahmad bin Ali Al-Ajmy is an acclaimed Saudi Qari from Khobar. Widely cherished across the Islamic world for his powerful, moving cadence.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/ahmad-ajmi/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://server10.mp3quran.net/ajm/%03d.mp3",
            listenerCount = "7.8M listeners",
            isFeatured = false,
            style = "Moving & Powerful Resonance",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_ajmi
        ),
        Reciter(
            id = "minshawi",
            name = "Sheikh Muhammad Siddiq Al-Minshawi",
            arabicName = "محمد صديق المنشاوي",
            country = "Egypt 🇪🇬",
            subtitle = "Egypt • The Weeping Voice",
            bio = "Sheikh Muhammad Siddiq Al-Minshawi was an internationally revered Egyptian Qari. Known affectionately across the Muslim world as 'The Weeping Voice'.",
            serverUrlPattern = "https://cdn.mp3quran.net/audio/muhammad-minshawi/r1/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/%03d.mp3",
            listenerCount = "8.7M listeners",
            isFeatured = true,
            style = "The Weeping Voice • Spiritual Purity",
            avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&auto=format&fit=crop&q=80",
            localDrawableRes = R.drawable.img_qari_minshawi
        )
    )

    fun getById(id: String): Reciter {
        return reciters.find { it.id == id } ?: reciters.first()
    }
}
