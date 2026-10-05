package com.example.data.model

data class Reciter(
    val id: String,
    val name: String,
    val arabicName: String,
    val country: String,
    val bio: String,
    val serverUrlPattern: String,
    val fallbackServerUrlPattern: String,
    val listenerCount: String,
    val isFeatured: Boolean = false,
    val style: String,
    val avatarUrl: String
) {
    fun getAudioUrl(surahNumber: Int): String {
        return String.format(serverUrlPattern, surahNumber)
    }

    fun getFallbackAudioUrl(surahNumber: Int): String {
        return String.format(fallbackServerUrlPattern, surahNumber)
    }
}

object ReciterData {
    val reciters: List<Reciter> = listOf(
        Reciter(
            id = "yasser",
            name = "Yasser Al-Dosari",
            arabicName = "ياسر الدوسري",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Dr. Yasser bin Rashid Al-Dosari is an Imam of the Grand Mosque in Makkah (Masjid al-Haram). He is renowned globally for his profoundly poignant, emotional, and resonant recitation of the Holy Quran.",
            serverUrlPattern = "https://server11.mp3quran.net/yasser/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/yasser_ad-dussary/%03d.mp3",
            listenerCount = "5.8M listeners",
            isFeatured = true,
            style = "Emotional & Majestic",
            avatarUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "afs",
            name = "Mishary Rashid Alafasy",
            arabicName = "مشاري بن راشد العفاسي",
            country = "Kuwait 🇰🇼",
            bio = "Sheikh Mishary bin Rashid Alafasy is a Kuwaiti qari, imam, preacher, and nasheed artist. He studied in the College of the Holy Quran and Islamic Studies at the Islamic University of Madinah and is widely recognized for his harmonious melodic cadence.",
            serverUrlPattern = "https://server8.mp3quran.net/afs/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/%03d.mp3",
            listenerCount = "7.2M listeners",
            isFeatured = false,
            style = "Harmonious & Melodic",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "sds",
            name = "Abdul Rahman Al-Sudais",
            arabicName = "عبد الرحمن السديس",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Abdul Rahman Ibn Abdul Aziz Al-Sudais is the General President for the Affairs of the Two Holy Mosques and chief imam of the Grand Mosque in Makkah. His iconic, rhythmic recitation is heard by millions across the globe daily.",
            serverUrlPattern = "https://server11.mp3quran.net/sds/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/abdurrahmaan_as-sudays/%03d.mp3",
            listenerCount = "6.1M listeners",
            isFeatured = false,
            style = "Iconic & Resonant",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "maher",
            name = "Maher Al-Muaiqly",
            arabicName = "ماهر المعيقلي",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Maher bin Hamad Al-Muaiqly is an Imam and Khateeb of the Grand Mosque in Makkah. Known for his deeply soothing, crystal-clear voice that brings immense calmness and tranquility to the heart of the listener.",
            serverUrlPattern = "https://server12.mp3quran.net/maher/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/maher_almu3aiqly/%03d.mp3",
            listenerCount = "4.9M listeners",
            isFeatured = false,
            style = "Calming & Tranquil",
            avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "s_gmd",
            name = "Saad Al-Ghamdi",
            arabicName = "سعد الغامدي",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Saad Al-Ghamdi is a Saudi scholar and celebrated reciter, famed for his smooth and steady tajweed recitation with unmatched rhythm and peace.",
            serverUrlPattern = "https://server7.mp3quran.net/s_gmd/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/sa3d_al-ghaamidee/%03d.mp3",
            listenerCount = "3.8M listeners",
            isFeatured = false,
            style = "Smooth & Peaceful",
            avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "husr",
            name = "Mahmoud Khalil Al-Husary",
            arabicName = "محمود خليل الحصري",
            country = "Egypt 🇪🇬",
            bio = "Sheikh Mahmoud Khalil Al-Husary was an Egyptian Qari widely acclaimed for his accurate recitation of the Qur'an. He was the first to record the Qur'an in the Murattal style and is revered as the master of Tajweed.",
            serverUrlPattern = "https://server13.mp3quran.net/husr/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/mahmood_khaleel_al-husaree_murattal/%03d.mp3",
            listenerCount = "3.5M listeners",
            isFeatured = false,
            style = "Classic Tajweed Master",
            avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "shatri",
            name = "Abu Bakr Al-Shatri",
            arabicName = "أبو بكر الشاطري",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Abu Bakr Ibn Mohamed Al Shatri is an acclaimed Saudi reciter and imam. His warm, gentle tone and reflective pace make his recitations deeply meditative.",
            serverUrlPattern = "https://server11.mp3quran.net/shatri/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/abu_bakr_ash-shaatree/%03d.mp3",
            listenerCount = "2.9M listeners",
            isFeatured = false,
            style = "Meditative & Warm",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "shur",
            name = "Saud Al-Shuraim",
            arabicName = "سعود الشريم",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Dr. Saud Ibn Ibrahim Ibn Muhammad Al-Shuraim was a longtime Imam and Khateeb of the Grand Mosque in Makkah. Known for his crisp articulation and reverent delivery.",
            serverUrlPattern = "https://server7.mp3quran.net/shur/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/sa3ood_al-shuraym/%03d.mp3",
            listenerCount = "2.7M listeners",
            isFeatured = false,
            style = "Reverent & Crisp",
            avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "qtm",
            name = "Nasser Al-Qatami",
            arabicName = "ناصر القطامي",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Nasser Al-Qatami is the imam of Princess Latifa Bint Sultan Mosque in Riyadh. His youthful, vibrant and heartfelt voice has touched millions worldwide.",
            serverUrlPattern = "https://server6.mp3quran.net/qtm/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/naaser_alqatamee/%03d.mp3",
            listenerCount = "3.1M listeners",
            isFeatured = false,
            style = "Vibrant & Heartfelt",
            avatarUrl = "https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "a_jbr",
            name = "Ali Jaber",
            arabicName = "علي جابر",
            country = "Saudi Arabia 🇸🇦",
            bio = "Sheikh Ali Jaber was the beloved Imam of the Grand Mosque in Makkah during the 1980s. His recitation is legendary for its spiritual gravity and serene melancholy.",
            serverUrlPattern = "https://server11.mp3quran.net/a_jbr/%03d.mp3",
            fallbackServerUrlPattern = "https://download.quranicaudio.com/quran/ali_jaber/%03d.mp3",
            listenerCount = "2.4M listeners",
            isFeatured = false,
            style = "Soulful & Spiritual",
            avatarUrl = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "kurdi",
            name = "Raad Mohammad Al-Kurdi",
            arabicName = "رعد محمد الكردي",
            country = "Iraq 🇮🇶",
            bio = "Sheikh Raad Al-Kurdi is an Iraqi Kurdish reciter based in Erbil. Known for his exceptionally calm, modern, and soulful tone that resonates deeply with younger listeners.",
            serverUrlPattern = "https://server6.mp3quran.net/kurdi/%03d.mp3",
            fallbackServerUrlPattern = "https://server6.mp3quran.net/kurdi/%03d.mp3",
            listenerCount = "3.3M listeners",
            isFeatured = false,
            style = "Modern & Gentle",
            avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=500&auto=format&fit=crop&q=80"
        ),
        Reciter(
            id = "abkr",
            name = "Idris Abkar",
            arabicName = "إدريس أبكر",
            country = "Yemen 🇾🇪",
            bio = "Sheikh Idris Abkar is an acclaimed reciter and imam of Sheikh Zayed Grand Mosque. His emotional, pleading recitation has made him one of the most beloved voices in the Islamic world.",
            serverUrlPattern = "https://server6.mp3quran.net/abkr/%03d.mp3",
            fallbackServerUrlPattern = "https://server6.mp3quran.net/abkr/%03d.mp3",
            listenerCount = "2.8M listeners",
            isFeatured = false,
            style = "Emotional & Uplifting",
            avatarUrl = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=500&auto=format&fit=crop&q=80"
        )
    )

    fun getById(id: String): Reciter {
        return reciters.find { it.id == id } ?: reciters.first()
    }
}
