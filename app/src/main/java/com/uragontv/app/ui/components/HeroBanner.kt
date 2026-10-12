package com.uragontv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.uragontv.app.data.model.Channel
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.LiveRed
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary
import com.uragontv.app.ui.theme.TextSecondary

@Composable
fun HeroBanner(
    channel: Channel?,
    onWatchClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (channel == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCharcoal)
            .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(16.dp))
    ) {
        // Background branding / preview
        Row(modifier = Modifier.fillMaxSize()) {
            // Left content info
            Column(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Category & Region tag badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LiveBadge()

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MetallicGold.copy(alpha = 0.2f))
                            .border(0.5.dp, MetallicGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.region,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftAntiqueGold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = channel.quality,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetallicGold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = channel.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description / Live program
                Text(
                    text = channel.description.ifEmpty { "Transmitting live HD broadcast across ${channel.region}. Press OK on TV remote to tune in." },
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Play Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TvFocusableCard(
                        onClick = { onWatchClick(channel) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp)),
                        focusedBackgroundColor = MetallicGold,
                        unfocusedBackgroundColor = Color(0x33D4AF37)
                    ) { isFocused ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isFocused) ObsidianBlack else SoftAntiqueGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WATCH LIVE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isFocused) ObsidianBlack else SoftAntiqueGold
                            )
                        }
                    }

                    Text(
                        text = "Press OK / Enter to Play",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Right logo / preview visual
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                DarkCharcoal,
                                Color(0x40D4AF37),
                                Color(0x1A000000)
                            )
                        )
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channel.logoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = channel.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = MetallicGold,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
        }
    }
}
