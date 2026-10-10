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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.ui.components.ReciterAvatar
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RecitersScreen(
    onSelectReciter: (Reciter) -> Unit,
    onQuickPlayReciter: (Reciter) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredReciters = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ReciterData.reciters
        } else {
            val q = searchQuery.trim().lowercase()
            ReciterData.reciters.filter {
                it.name.lowercase().contains(q) ||
                it.arabicName.contains(q) ||
                it.country.lowercase().contains(q) ||
                it.style.lowercase().contains(q)
            }
        }
    }

    val featuredReciter = remember {
        ReciterData.reciters.find { it.isFeatured } ?: ReciterData.reciters.first()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // SCREEN TITLE
        Text(
            text = "Reciters",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )

        // SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search reciters...",
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
                .padding(vertical = 10.dp)
                .testTag("reciters_search_input")
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // FEATURED RECITER AT TOP (Full width)
            if (searchQuery.isBlank()) {
                item(span = { GridItemSpan(3) }) {
                    FeaturedReciterHeroCard(
                        reciter = featuredReciter,
                        onCardClick = { onSelectReciter(featuredReciter) },
                        onPlayClick = { onQuickPlayReciter(featuredReciter) }
                    )
                }

                item(span = { GridItemSpan(3) }) {
                    Text(
                        text = "All Reciters (${ReciterData.reciters.size})",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
            }

            // 3-COLUMN GRID OF RECITERS
            items(filteredReciters, key = { it.id }) { reciter ->
                ReciterGridItem(
                    reciter = reciter,
                    onClick = { onSelectReciter(reciter) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedReciterHeroCard(
    reciter: Reciter,
    onCardClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF262118),
                        Color(0xFF14151C)
                    )
                )
            )
            .border(1.2.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .clickable { onCardClick() }
            .padding(18.dp)
            .testTag("featured_reciter_card")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GoldPrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FEATURED RECITER",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large Circular Photo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(2.5.dp, GoldPrimary, CircleShape)
            ) {
                ReciterAvatar(
                    reciter = reciter,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name & Arabic Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = reciter.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Verified",
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = reciter.arabicName,
                color = GoldPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${reciter.country} • ${reciter.listenerCount}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(GoldPrimary)
                    .clickable { onPlayClick() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color(0xFF090A0E),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Play Recitations",
                    color = Color(0xFF090A0E),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ReciterGridItem(
    reciter: Reciter,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .testTag("reciter_grid_${reciter.id}")
    ) {
        // Circular Avatar
        Box(
            modifier = Modifier
                .size(78.dp)
                .clip(CircleShape)
                .background(SurfaceCard)
                .border(1.5.dp, SurfaceCardBorder, CircleShape)
        ) {
            ReciterAvatar(
                reciter = reciter,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        Text(
            text = reciter.name,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = reciter.country.split(" ").lastOrNull() ?: "",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}
