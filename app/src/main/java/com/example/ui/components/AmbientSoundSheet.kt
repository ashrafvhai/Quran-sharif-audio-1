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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmbientSound
import com.example.data.model.AmbientSoundData
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbientSoundSheet(
    selectedSound: AmbientSound,
    ambientVolume: Float,
    onSoundSelected: (AmbientSound) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredSounds = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            AmbientSoundData.sounds
        } else {
            AmbientSoundData.sounds.filter {
                it.name.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF213141C),
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
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ambient Sounds",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calming atmosphere mixed beneath recitation",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_ambient_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Ambient Volume Slider Section
            if (selectedSound.id != "none") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Ambient Volume",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Ambient Mix Level",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${(ambientVolume * 100).toInt()}%",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = ambientVolume.coerceIn(0f, 1f),
                            onValueChange = { onVolumeChange(it.coerceIn(0f, 1f)) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = GoldPrimary,
                                activeTrackColor = GoldPrimary,
                                inactiveTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.testTag("ambient_volume_slider")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search background sounds...",
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
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
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
                    .testTag("ambient_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3-Column Grid of 14 Ambient Sounds
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredSounds, key = { it.id }) { sound ->
                    val isSelected = sound.id == selectedSound.id
                    AmbientSoundCard(
                        sound = sound,
                        isSelected = isSelected,
                        onClick = { onSoundSelected(sound) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AmbientSoundCard(
    sound: AmbientSound,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) GoldPrimary else SurfaceCardBorder
    val bgModifier = if (isSelected) {
        Modifier.background(
            Brush.verticalGradient(
                listOf(
                    GoldPrimary.copy(alpha = 0.25f),
                    Color(0xFF262015)
                )
            )
        )
    } else {
        Modifier.background(SurfaceCard)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(bgModifier)
            .border(if (isSelected) 1.8.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 6.dp)
            .testTag("ambient_card_${sound.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color(0x18FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sound.emoji,
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sound.name,
                color = if (isSelected) GoldPrimary else TextPrimary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
