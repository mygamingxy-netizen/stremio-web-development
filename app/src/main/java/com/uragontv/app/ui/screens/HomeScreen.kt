package com.uragontv.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uragontv.app.data.model.Channel
import com.uragontv.app.ui.components.ChannelCard
import com.uragontv.app.ui.components.HeroBanner
import com.uragontv.app.ui.theme.DarkBackground
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    channels: List<Channel>,
    isLoading: Boolean,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    var highlightedChannel by remember(channels) {
        mutableStateOf(channels.firstOrNull())
    }

    if (isLoading && channels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ObsidianBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MetallicGold, strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading Uragon TV Channels...",
                    color = SoftAntiqueGold,
                    fontSize = 14.sp
                )
            }
        }
        return
    }

    // Channel groups
    val featuredChannels = remember(channels) {
        channels.filter { it.isFavorite || it.quality.contains("60fps") || it.id in listOf("ptv4", "gma7", "bicol_tv", "davao_tv") }
            .ifEmpty { channels.take(6) }
    }
    val nationalChannels = remember(channels) {
        channels.filter { it.category == "National Networks" || it.region == "Nationwide" }
    }
    val regionalChannels = remember(channels) {
        channels.filter { it.category == "Regional / Local" || it.region in listOf("Luzon", "Visayas", "Mindanao") }
    }
    val newsChannels = remember(channels) {
        channels.filter { it.category.contains("News", ignoreCase = true) || it.name.contains("News", ignoreCase = true) }
    }
    val sportsChannels = remember(channels) {
        channels.filter { it.category.contains("Sports", ignoreCase = true) || it.name.contains("Sports", ignoreCase = true) }
    }
    val entertainmentChannels = remember(channels) {
        channels.filter { it.category in listOf("Entertainment", "Movies") }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Featured Banner
        item {
            HeroBanner(
                channel = highlightedChannel ?: channels.firstOrNull(),
                onWatchClick = onChannelClick
            )
        }

        // Featured & Live Now Row
        item {
            ChannelRailSection(
                title = "⭐ FEATURED & LIVE NOW",
                subtitle = "Top national and high-definition live channels",
                channels = featuredChannels,
                onChannelClick = onChannelClick,
                onChannelFocus = { ch -> highlightedChannel = ch }
            )
        }

        // National Networks Row
        if (nationalChannels.isNotEmpty()) {
            item {
                ChannelRailSection(
                    title = "🇵🇭 NATIONAL NETWORKS",
                    subtitle = "Major nationwide television broadcasts",
                    channels = nationalChannels,
                    onChannelClick = onChannelClick,
                    onChannelFocus = { ch -> highlightedChannel = ch }
                )
            }
        }

        // Regional & Local Streams Row
        if (regionalChannels.isNotEmpty()) {
            item {
                ChannelRailSection(
                    title = "🌴 REGIONAL & LOCAL STREAMS",
                    subtitle = "Luzon, Visayas & Mindanao community feeds",
                    channels = regionalChannels,
                    onChannelClick = onChannelClick,
                    onChannelFocus = { ch -> highlightedChannel = ch }
                )
            }
        }

        // 24/7 News Row
        if (newsChannels.isNotEmpty()) {
            item {
                ChannelRailSection(
                    title = "📰 NEWS 24/7",
                    subtitle = "Real-time news broadcasts & international reports",
                    channels = newsChannels,
                    onChannelClick = onChannelClick,
                    onChannelFocus = { ch -> highlightedChannel = ch }
                )
            }
        }

        // Sports Row
        if (sportsChannels.isNotEmpty()) {
            item {
                ChannelRailSection(
                    title = "⚽ SPORTS & ACTION",
                    subtitle = "Extreme sports, basketball, volleyball & tournaments",
                    channels = sportsChannels,
                    onChannelClick = onChannelClick,
                    onChannelFocus = { ch -> highlightedChannel = ch }
                )
            }
        }

        // Movies & Entertainment Row
        if (entertainmentChannels.isNotEmpty()) {
            item {
                ChannelRailSection(
                    title = "🎬 MOVIES & ENTERTAINMENT",
                    subtitle = "Pinoy cinema classics, music channels & documentaries",
                    channels = entertainmentChannels,
                    onChannelClick = onChannelClick,
                    onChannelFocus = { ch -> highlightedChannel = ch }
                )
            }
        }
    }
}

@Composable
private fun ChannelRailSection(
    title: String,
    subtitle: String,
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
    onChannelFocus: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SoftAntiqueGold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            Text(
                text = "${channels.size} Channels",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MetallicGold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                ChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel) },
                    onFocusChange = { isFocused ->
                        if (isFocused) {
                            onChannelFocus(channel)
                        }
                    }
                )
            }
        }
    }
}
