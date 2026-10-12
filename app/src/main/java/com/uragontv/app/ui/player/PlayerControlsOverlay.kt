package com.uragontv.app.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.uragontv.app.data.model.AspectRatioMode
import com.uragontv.app.data.model.Channel
import com.uragontv.app.ui.components.LiveBadge
import com.uragontv.app.ui.components.TvFocusableCard
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.SurfaceElevated
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary
import com.uragontv.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlayerControlsOverlay(
    channel: Channel?,
    isVisible: Boolean,
    isBuffering: Boolean,
    isReconnecting: Boolean,
    errorMessage: String?,
    isFavorite: Boolean,
    aspectRatioMode: AspectRatioMode,
    streamResolution: String,
    isQuickPickerVisible: Boolean,
    allChannels: List<Channel>,
    onBackClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCycleAspectRatio: () -> Unit,
    onToggleQuickPicker: () -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onPreviousChannel: () -> Unit,
    onNextChannel: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Buffering / Loading Indicator in center
        if (isBuffering || isReconnecting) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xD90B0C10))
                    .border(1.dp, MetallicGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 28.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = SoftAntiqueGold,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isReconnecting) "Reconnecting Live Stream..." else "Buffering Stream...",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (channel != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = channel.name,
                            color = SoftAntiqueGold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Error message banner with Retry option
        if (errorMessage != null && !isBuffering && !isReconnecting) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xEE1E1E24))
                    .border(1.dp, Color(0xFFE50914), RoundedCornerShape(14.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⚠️ STREAM PLAYBACK NOTICE",
                        color = SoftAntiqueGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TvFocusableCard(
                        onClick = onRetry,
                        focusedBackgroundColor = MetallicGold,
                        unfocusedBackgroundColor = DarkCharcoal,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) { isFocused ->
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
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
                                text = "RELOAD STREAM",
                                color = if (isFocused) ObsidianBlack else SoftAntiqueGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // OSD Controls Overlay (Animated on D-Pad interaction)
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xEE0B0C10), Color(0x990B0C10), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 32.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back to Guide button
                        TvFocusableCard(
                            onClick = onBackClick,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                            focusedBackgroundColor = MetallicGold,
                            unfocusedBackgroundColor = Color(0x661E1E24)
                        ) { isFocused ->
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (isFocused) ObsidianBlack else TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TV GUIDE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFocused) ObsidianBlack else TextPrimary
                                )
                            }
                        }

                        // Right info: Region / Time
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (channel != null) {
                                Text(
                                    text = "${channel.category} • ${channel.region}",
                                    color = SoftAntiqueGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Text(
                                text = currentTime,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom bar: Channel info & action controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    // Quick Channel Picker if toggled
                    if (isQuickPickerVisible) {
                        QuickChannelPicker(
                            channels = allChannels,
                            currentChannelId = channel?.id,
                            onChannelSelected = onChannelSelected
                        )
                    }

                    // Main bottom control bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xB30B0C10), Color(0xF20B0C10))
                                )
                            )
                            .padding(horizontal = 32.dp, vertical = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Channel Branding
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (channel != null && channel.logoUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(channel.logoUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = channel.name,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkCharcoal)
                                            .padding(6.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkCharcoal),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tv,
                                            contentDescription = null,
                                            tint = MetallicGold,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        LiveBadge()
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0x33D4AF37))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = streamResolution,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SoftAntiqueGold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = channel?.name ?: "Uragon TV",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Text(
                                        text = channel?.description ?: "Broadcasting live across the Philippines",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Right Action Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Previous Channel
                                TvFocusableCard(
                                    onClick = onPreviousChannel,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = Color(0x661E1E24)
                                ) { isFocused ->
                                    Box(modifier = Modifier.padding(10.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.SkipPrevious,
                                            contentDescription = "Previous Channel",
                                            tint = if (isFocused) ObsidianBlack else TextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Next Channel
                                TvFocusableCard(
                                    onClick = onNextChannel,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = Color(0x661E1E24)
                                ) { isFocused ->
                                    Box(modifier = Modifier.padding(10.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.SkipNext,
                                            contentDescription = "Next Channel",
                                            tint = if (isFocused) ObsidianBlack else TextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Quick Channel List button
                                TvFocusableCard(
                                    onClick = onToggleQuickPicker,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = if (isQuickPickerVisible) Color(0x33D4AF37) else Color(0x661E1E24)
                                ) { isFocused ->
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.List,
                                            contentDescription = "Channel List",
                                            tint = if (isFocused) ObsidianBlack else SoftAntiqueGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CHANNELS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFocused) ObsidianBlack else SoftAntiqueGold
                                        )
                                    }
                                }

                                // Aspect ratio toggle
                                TvFocusableCard(
                                    onClick = onCycleAspectRatio,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = Color(0x661E1E24)
                                ) { isFocused ->
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AspectRatio,
                                            contentDescription = "Aspect Ratio",
                                            tint = if (isFocused) ObsidianBlack else TextPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = aspectRatioMode.label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFocused) ObsidianBlack else TextPrimary
                                        )
                                    }
                                }

                                // Favorite Toggle
                                TvFocusableCard(
                                    onClick = onToggleFavorite,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = Color(0x661E1E24)
                                ) { isFocused ->
                                    Box(modifier = Modifier.padding(10.dp)) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Favorite",
                                            tint = if (isFocused) ObsidianBlack else SoftAntiqueGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Refresh Stream
                                TvFocusableCard(
                                    onClick = onRetry,
                                    modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                    focusedBackgroundColor = MetallicGold,
                                    unfocusedBackgroundColor = Color(0x661E1E24)
                                ) { isFocused ->
                                    Box(modifier = Modifier.padding(10.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Reload",
                                            tint = if (isFocused) ObsidianBlack else TextPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
