package com.uragontv.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uragontv.app.data.model.Channel
import com.uragontv.app.ui.components.ChannelCard
import com.uragontv.app.ui.components.TvFocusableCard
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary
import com.uragontv.app.ui.theme.TextSecondary

@Composable
fun CategoriesScreen(
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember(channels) {
        listOf("All Categories") + channels.map { it.category }.distinct()
    }

    var selectedCategory by remember { mutableStateOf("All Categories") }

    val filteredChannels = remember(channels, selectedCategory) {
        if (selectedCategory == "All Categories") {
            channels
        } else {
            channels.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CATEGORIES & GENRES",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftAntiqueGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Browse channels filtered by broadcast format and content type",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            Text(
                text = "${filteredChannels.size} Channels in $selectedCategory",
                fontSize = 12.sp,
                color = MetallicGold,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Category Selector Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat

                TvFocusableCard(
                    onClick = { selectedCategory = cat },
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                    scaleFactor = 1.05f,
                    focusedBackgroundColor = MetallicGold,
                    unfocusedBackgroundColor = if (isSelected) Color(0x33D4AF37) else DarkCharcoal,
                    focusedBorderColor = SoftAntiqueGold,
                    unfocusedBorderColor = if (isSelected) MetallicGold else Color(0x22FFFFFF)
                ) { isFocused ->
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (isFocused) ObsidianBlack else if (isSelected) SoftAntiqueGold else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Channel Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredChannels, key = { it.id }) { channel ->
                ChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel) }
                )
            }
        }
    }
}
