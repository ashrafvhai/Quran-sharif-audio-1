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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.AudioPlayerManager
import com.example.player.PlayerUiState
import com.example.ui.theme.AppCustomTheme
import com.example.ui.theme.ThemeManager

@Composable
fun SettingsScreen(
    playerState: PlayerUiState,
    playerManager: AudioPlayerManager,
    modifier: Modifier = Modifier
) {
    val currentTheme by ThemeManager.currentTheme.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text("Settings & Customization", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("অডিও প্লেব্যাক এবং থিম পছন্দসমূহ", color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Background Audio info
            item {
                Text("Playback & Background Audio", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x2E10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Background Audio & Controls", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "অ্যাপ মিনিমাইজ বা স্ক্রিন বন্ধ করলেও ব্যাকগ্রাউন্ডে অবিরাম তিলাওয়াত চলবে এবং নোটিফিকেশন বার থেকে সহজে নিয়ন্ত্রণ করা যাবে।",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Reset queue order button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .clickable { playerManager.resetQueueToDefault() }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Reset Queue Sequence (১-১১৪ ক্রম)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("প্লেলিস্টের ক্রমকে মূল সূরা ক্রম অনুসারে সাজাও", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Themes Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Visual Glass Themes (গ্লাস থিম)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(AppCustomTheme.entries.size) { idx ->
                val theme = AppCustomTheme.entries[idx]
                val isSelected = theme == currentTheme

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0x33FFFFFF) else Color(0x14FFFFFF))
                        .border(
                            1.dp,
                            if (isSelected) theme.primaryColor else Color.White.copy(alpha = 0.1f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { ThemeManager.setTheme(theme) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(theme.primaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(theme.emoji, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(theme.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(theme.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }

                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // About App section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x0FFFFFFF))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Easy Quranify v1.0", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ultra-optimized Android App • Continuous Playback", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
