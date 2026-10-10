package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AmbientSound
import com.example.data.model.AmbientSoundData
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import com.example.player.PlayerUiState
import com.example.ui.components.ReciterAvatar
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    playerState: PlayerUiState,
    onPlaySurah: (Surah, Reciter, AmbientSound) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Meccan", "Medinan", "Juz 'Amma")

    val displayedSurahs = remember(selectedFilter) {
        when (selectedFilter) {
            "Meccan" -> QuranData.surahs.filter { it.revelationType == "Meccan" }
            "Medinan" -> QuranData.surahs.filter { it.revelationType == "Medinan" }
            "Juz 'Amma" -> QuranData.surahs.filter { it.juzNumber == 30 }
            else -> QuranData.surahs.take(15)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // TOP APP BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldPrimary, Color(0xFF996B12))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📖",
                            fontSize = 18.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Easy Quranify",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Holy Quran Recitations",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, CircleShape)
                        .testTag("home_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Surahs and Reciters",
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // VERSE OF THE DAY CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF2C2415),
                                Color(0xFF1A1B24)
                            )
                        )
                    )
                    .border(1.2.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = "Daily Verse",
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "DAILY INSPIRATION",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "Surah Ar-Ra'd • 28",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        lineHeight = 34.sp,
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "\"Those who have believed and whose hearts are assured by the remembrance of Allah. Unquestionably, by the remembrance of Allah hearts are assured.\"",
                        color = TextPrimary.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldPrimary)
                            .clickable {
                                val radSurah = QuranData.surahs.find { it.number == 13 } ?: QuranData.surahs.first()
                                onPlaySurah(radSurah, playerState.currentReciter, playerState.ambientSound)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("play_daily_verse"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Listen",
                            tint = Color(0xFF090A0E),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Listen to Surah Ar-Ra'd",
                            color = Color(0xFF090A0E),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // FEATURED RECITERS ROW
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Beloved Reciters",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ReciterData.reciters) { reciter ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onSelectReciter(reciter) }
                                .testTag("home_reciter_${reciter.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, if (reciter.id == playerState.currentReciter.id) GoldPrimary else SurfaceCardBorder, CircleShape)
                            ) {
                                ReciterAvatar(
                                    reciter = reciter,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = reciter.name.split(" ").firstOrNull() ?: reciter.name,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // BROWSE SURAHS SECTION WITH FILTER CHIPS
        item {
            Column {
                Text(
                    text = "Explore Surahs",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedFilter == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldPrimary else SurfaceCard)
                                .clickable { selectedFilter = cat }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFF090A0E) else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // SURAH CARDS LIST
        items(displayedSurahs, key = { it.number }) { surah ->
            val isPlayingThis = playerState.isPlaying && playerState.currentSurah.number == surah.number

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isPlayingThis) GoldPrimary.copy(alpha = 0.12f) else SurfaceCard)
                    .border(
                        1.dp,
                        if (isPlayingThis) GoldPrimary.copy(alpha = 0.8f) else SurfaceCardBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        onPlaySurah(surah, playerState.currentReciter, playerState.ambientSound)
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("home_surah_${surah.number}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingThis) GoldPrimary else Color(0x18FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlayingThis) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = "Playing",
                                tint = Color(0xFF090A0E),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${surah.number}",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = surah.nameEnglish,
                            color = if (isPlayingThis) GoldPrimary else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${surah.nameTranslation} • ${surah.versesCount} verses",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = surah.nameArabic,
                        color = if (isPlayingThis) GoldPrimary else TextPrimary.copy(alpha = 0.85f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
