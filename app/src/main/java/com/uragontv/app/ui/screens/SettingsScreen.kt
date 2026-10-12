package com.uragontv.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uragontv.app.data.model.BufferProfile
import com.uragontv.app.data.repository.StreamSourceRepository
import com.uragontv.app.ui.components.TvFocusableCard
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.SurfaceElevated
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary
import com.uragontv.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    repository: StreamSourceRepository,
    totalChannelsCount: Int,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentSourceUrl by repository.sourceUrl.collectAsState()
    val bufferProfile by repository.bufferProfile.collectAsState()
    val autoReconnect by repository.autoReconnect.collectAsState()

    var statusMessage by remember { mutableStateOf<String?>(null) }

    val presetSources = remember {
        listOf(
            "asset://sources.m3u" to "Local M3U Playlist (Standard)",
            "asset://channels.json" to "Local JSON Catalog (Fallback)",
            "https://raw.githubusercontent.com/iptv-org/iptv/master/streams/ph.m3u" to "IPTV-Org Philippines (Remote Feed)"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = SoftAntiqueGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "URAGON TV SETTINGS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftAntiqueGold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Configure playlist sources, streaming buffer, reconnection logic, and system engine",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Reload Playlist Button
            TvFocusableCard(
                onClick = {
                    coroutineScope.launch {
                        statusMessage = "Reloading channels..."
                        repository.loadChannels()
                        statusMessage = "Playlist reloaded successfully!"
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                focusedBackgroundColor = MetallicGold,
                unfocusedBackgroundColor = Color(0x33D4AF37)
            ) { isFocused ->
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = if (isFocused) ObsidianBlack else SoftAntiqueGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RELOAD SOURCES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFocused) ObsidianBlack else SoftAntiqueGold
                    )
                }
            }
        }

        if (statusMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x3300E676))
                    .border(1.dp, Color(0xFF00E676), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = statusMessage!!,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 1: M3U / JSON Stream Source Selector
        Text(
            text = "📡 PLAYLIST & STREAM SOURCE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftAntiqueGold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Select active stream source or inject remote M3U URL",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            presetSources.forEach { (url, label) ->
                val isSelected = currentSourceUrl == url

                TvFocusableCard(
                    onClick = {
                        repository.setSourceUrl(url)
                        coroutineScope.launch {
                            repository.loadChannels(url)
                            statusMessage = "Switched to: $label"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    focusedBackgroundColor = SurfaceElevated,
                    unfocusedBackgroundColor = if (isSelected) Color(0x26D4AF37) else DarkCharcoal,
                    focusedBorderColor = SoftAntiqueGold,
                    unfocusedBorderColor = if (isSelected) MetallicGold else Color(0x22FFFFFF)
                ) { isFocused ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFocused) SoftAntiqueGold else if (isSelected) MetallicGold else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = url,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = SoftAntiqueGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 2: Buffer Profile
        Text(
            text = "⏱️ EXOPLAYER BUFFER PROFILE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftAntiqueGold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = "Adjust buffer duration to optimize for low latency or stability on slow Wi-Fi",
            fontSize = 11.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BufferProfile.values().forEach { profile ->
                val isSelected = bufferProfile == profile

                TvFocusableCard(
                    onClick = {
                        repository.setBufferProfile(profile)
                        statusMessage = "Buffer profile set to ${profile.label}"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    focusedBackgroundColor = SurfaceElevated,
                    unfocusedBackgroundColor = if (isSelected) Color(0x26D4AF37) else DarkCharcoal,
                    focusedBorderColor = SoftAntiqueGold,
                    unfocusedBorderColor = if (isSelected) MetallicGold else Color(0x22FFFFFF)
                ) { isFocused ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = profile.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFocused) SoftAntiqueGold else if (isSelected) MetallicGold else TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${profile.minBufferMs / 1000}s - ${profile.maxBufferMs / 1000}s buffer window",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 3: Auto-Reconnect Toggle
        Text(
            text = "🔄 STREAM RESILIENCE",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftAntiqueGold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        TvFocusableCard(
            onClick = {
                val newVal = !autoReconnect
                repository.setAutoReconnect(newVal)
                statusMessage = if (newVal) "Auto-reconnect enabled" else "Auto-reconnect disabled"
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            focusedBackgroundColor = SurfaceElevated,
            unfocusedBackgroundColor = DarkCharcoal
        ) { isFocused ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Automatic Stream Reconnect",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFocused) SoftAntiqueGold else TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Automatically retries stream connection when live broadcast drops or buffers",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (autoReconnect) MetallicGold else Color(0x33FFFFFF))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (autoReconnect) "ENABLED" else "DISABLED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (autoReconnect) ObsidianBlack else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 4: System Diagnostics
        Text(
            text = "ℹ️ SYSTEM & ENGINE DIAGNOSTICS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SoftAntiqueGold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCharcoal)
                .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DiagnosticRow("Application Name", "UragonTv Live Hub")
                DiagnosticRow("App Version", "v1.0.0 (Android TV Edition)")
                DiagnosticRow("Target Platform", "Android TV / Google TV OS")
                DiagnosticRow("Playback Engine", "AndroidX Media3 ExoPlayer 1.5.1 (HLS & DASH)")
                DiagnosticRow("Total Channels Loaded", "$totalChannelsCount Live Channels")
                DiagnosticRow("Hardware Acceleration", "Enabled (MediaCodec Surface)")
                DiagnosticRow("D-Pad Remote Navigation", "Fully Supported (1.08x Scale & Gold Focus)")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SoftAntiqueGold
        )
    }
}
