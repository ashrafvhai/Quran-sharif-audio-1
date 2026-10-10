package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import com.example.ui.components.ReciterAvatar
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchSheet(
    onSelectSurah: (Surah) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val matchedSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            QuranData.surahs.filter {
                it.nameEnglish.lowercase().contains(q) ||
                it.nameArabic.contains(q) ||
                it.nameTranslation.lowercase().contains(q) ||
                it.number.toString() == q
            }.take(8)
        }
    }

    val matchedReciters = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            ReciterData.reciters.filter {
                it.name.lowercase().contains(q) ||
                it.arabicName.contains(q) ||
                it.country.lowercase().contains(q)
            }.take(5)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF713141D),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x44FFFFFF))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Quran & Reciters",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search Surah (e.g. Kahf, Rahman) or Reciter...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = GoldPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                contentPadding = PaddingValues(bottom = 36.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                if (searchQuery.isBlank()) {
                    item {
                        Text(
                            text = "Popular Searches:",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    val popular = listOf("Surah Al-Mulk", "Surah Yasin", "Surah Ar-Rahman", "Mishary Alafasy", "Yasser Al-Dosari")
                    items(popular) { tag ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceCard)
                                .clickable { searchQuery = tag.removePrefix("Surah ") }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(text = "🔍 $tag", color = TextPrimary, fontSize = 14.sp)
                        }
                    }
                } else {
                    if (matchedReciters.isNotEmpty()) {
                        item {
                            Text(
                                text = "Reciters",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }
                        items(matchedReciters, key = { "r_${it.id}" }) { reciter ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceCard)
                                    .clickable {
                                        onSelectReciter(reciter)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ReciterAvatar(
                                        reciter = reciter,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, GoldPrimary, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = reciter.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = reciter.country, color = TextMuted, fontSize = 12.sp)
                                    }
                                    Text(text = reciter.arabicName, color = GoldPrimary, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    if (matchedSurahs.isNotEmpty()) {
                        item {
                            Text(
                                text = "Surahs",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(matchedSurahs, key = { "s_${it.number}" }) { surah ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceCard)
                                    .clickable {
                                        onSelectSurah(surah)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(GoldPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "${surah.number}", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = surah.nameEnglish, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "${surah.nameTranslation} • ${surah.revelationType}", color = TextMuted, fontSize = 12.sp)
                                    }
                                    Text(text = surah.nameArabic, color = GoldPrimary, fontSize = 15.sp)
                                }
                            }
                        }
                    }

                    if (matchedReciters.isEmpty() && matchedSurahs.isEmpty()) {
                        item {
                            Text(
                                text = "No matching surahs or reciters found.",
                                color = TextMuted,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
