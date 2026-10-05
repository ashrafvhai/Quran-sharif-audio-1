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
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranData
import com.example.data.model.Surah
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahQueueSheet(
    currentSurah: Surah,
    isPlaying: Boolean,
    onSurahSelected: (Surah) -> Unit,
    downloadedSurahs: Set<Int>,
    downloadingSurahs: Set<Int>,
    onDownloadClick: (Surah) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            QuranData.surahs
        } else {
            val q = searchQuery.trim().lowercase()
            QuranData.surahs.filter {
                it.nameEnglish.lowercase().contains(q) ||
                it.nameArabic.contains(q) ||
                it.nameTranslation.lowercase().contains(q) ||
                it.number.toString() == q
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF212131A),
        tonalElevation = 8.dp,
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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Surah Queue",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "114 Surahs • Tap any to play",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_queue_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Filter surahs by name or number...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
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
                    .testTag("queue_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Queue List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    val isCurrent = surah.number == currentSurah.number
                    val isDownloaded = downloadedSurahs.contains(surah.number)
                    val isDownloading = downloadingSurahs.contains(surah.number)

                    QueueSurahItem(
                        surah = surah,
                        isCurrent = isCurrent,
                        isPlaying = isPlaying,
                        isDownloaded = isDownloaded,
                        isDownloading = isDownloading,
                        onClick = { onSurahSelected(surah) },
                        onDownloadClick = { onDownloadClick(surah) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueSurahItem(
    surah: Surah,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    val bgModifier = if (isCurrent) {
        Modifier.background(GoldPrimary.copy(alpha = 0.12f))
    } else {
        Modifier.background(SurfaceCard.copy(alpha = 0.6f))
    }

    val borderModifier = if (isCurrent) {
        Modifier.border(1.2.dp, GoldPrimary.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .then(bgModifier)
            .then(borderModifier)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("queue_item_${surah.number}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Medallion
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isCurrent) GoldPrimary else Color(0x18FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent && isPlaying) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = "Playing",
                    tint = Color(0xFF090A0E),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "${surah.number}",
                    color = if (isCurrent) Color(0xFF090A0E) else GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${surah.number}. ${surah.nameEnglish}",
                color = if (isCurrent) GoldPrimary else TextPrimary,
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${surah.nameTranslation} • ${surah.versesCount} verses • ${surah.revelationType}",
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Arabic Name
        Text(
            text = surah.nameArabic,
            color = if (isCurrent) GoldPrimary else TextPrimary.copy(alpha = 0.85f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Download Action
        if (isDownloading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .padding(6.dp),
                color = GoldPrimary,
                strokeWidth = 2.dp
            )
        } else {
            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.FileDownload,
                    contentDescription = "Download Surah",
                    tint = if (isDownloaded) GoldPrimary else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
