package com.uragontv.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uragontv.app.ui.navigation.Screen
import com.uragontv.app.ui.theme.DarkBackground
import com.uragontv.app.ui.theme.DarkCharcoal
import com.uragontv.app.ui.theme.GrayBorder
import com.uragontv.app.ui.theme.MetallicGold
import com.uragontv.app.ui.theme.ObsidianBlack
import com.uragontv.app.ui.theme.SoftAntiqueGold
import com.uragontv.app.ui.theme.TextMuted
import com.uragontv.app.ui.theme.TextPrimary
import com.uragontv.app.ui.theme.TextSecondary

data class NavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

@Composable
fun SidebarMenu(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = remember {
        listOf(
            NavItem(Screen.Home, "Home", Icons.Default.Home),
            NavItem(Screen.Categories, "Categories", Icons.Default.Category),
            NavItem(Screen.Regions, "Regions", Icons.Default.Explore),
            NavItem(Screen.Favorites, "Favorites", Icons.Default.Favorite),
            NavItem(Screen.Search, "Search", Icons.Default.Search),
            NavItem(Screen.Settings, "Settings", Icons.Default.Settings)
        )
    }

    var isSidebarFocused by remember { mutableStateOf(false) }

    val sidebarWidth by animateDpAsState(
        targetValue = if (isSidebarFocused) 210.dp else 74.dp,
        label = "SidebarWidth"
    )

    Column(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .background(DarkBackground)
            .border(width = 0.5.dp, color = GrayBorder.copy(alpha = 0.5f))
            .padding(vertical = 20.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // App Branding Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MetallicGold)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "UragonTv",
                    tint = ObsidianBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isSidebarFocused) {
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "URAGON TV",
                        color = SoftAntiqueGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Live Stream Hub",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Items
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = currentScreen == item.screen
                var isItemFocused by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                isItemFocused -> MetallicGold
                                isSelected -> DarkCharcoal
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = if (isItemFocused) 1.5.dp else if (isSelected) 1.dp else 0.dp,
                            color = if (isItemFocused) SoftAntiqueGold else if (isSelected) MetallicGold.copy(alpha = 0.4f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .onFocusChanged { focusState ->
                            isItemFocused = focusState.isFocused
                            // Track if any sidebar item has focus
                            if (focusState.isFocused) {
                                isSidebarFocused = true
                            }
                        }
                        .focusable()
                        .clickable {
                            onNavigate(item.screen)
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = when {
                                isItemFocused -> ObsidianBlack
                                isSelected -> MetallicGold
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(22.dp)
                        )

                        if (isSidebarFocused) {
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = item.title,
                                color = when {
                                    isItemFocused -> ObsidianBlack
                                    isSelected -> TextPrimary
                                    else -> TextSecondary
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isSelected || isItemFocused) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Live status indicator in sidebar
        if (isSidebarFocused) {
            LiveBadge(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
