package com.uragontv.app.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.uragontv.app.data.repository.FavoritesRepository
import com.uragontv.app.data.repository.StreamSourceRepository
import com.uragontv.app.ui.components.SidebarMenu
import com.uragontv.app.ui.player.PlayerScreen
import com.uragontv.app.ui.player.PlayerViewModel
import com.uragontv.app.ui.screens.CategoriesScreen
import com.uragontv.app.ui.screens.FavoritesScreen
import com.uragontv.app.ui.screens.HomeScreen
import com.uragontv.app.ui.screens.RegionsScreen
import com.uragontv.app.ui.screens.SearchScreen
import com.uragontv.app.ui.screens.SettingsScreen
import com.uragontv.app.ui.theme.ObsidianBlack

@UnstableApi
@Composable
fun TvNavHost(
    streamSourceRepository: StreamSourceRepository,
    favoritesRepository: FavoritesRepository,
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val channels by streamSourceRepository.channels.collectAsState()
    val isLoading by streamSourceRepository.isLoading.collectAsState()
    val favoriteIds by favoritesRepository.favoriteIds.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var activePlayingChannelId by remember { mutableStateOf<String?>(null) }

    // If currently in Player mode
    if (activePlayingChannelId != null) {
        PlayerScreen(
            channelId = activePlayingChannelId!!,
            viewModel = playerViewModel,
            onBackClick = {
                activePlayingChannelId = null
            },
            modifier = modifier
        )
        return
    }

    // Back button handling on secondary screens
    if (currentScreen != Screen.Home) {
        BackHandler {
            currentScreen = Screen.Home
        }
    }

    // TV Layout: Collapsible Sidebar + Content screen
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        SidebarMenu(
            currentScreen = currentScreen,
            onNavigate = { screen ->
                currentScreen = screen
            }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            when (currentScreen) {
                Screen.Home -> {
                    HomeScreen(
                        channels = channels,
                        isLoading = isLoading,
                        onChannelClick = { ch ->
                            activePlayingChannelId = ch.id
                        }
                    )
                }
                Screen.Categories -> {
                    CategoriesScreen(
                        channels = channels,
                        onChannelClick = { ch ->
                            activePlayingChannelId = ch.id
                        }
                    )
                }
                Screen.Regions -> {
                    RegionsScreen(
                        channels = channels,
                        onChannelClick = { ch ->
                            activePlayingChannelId = ch.id
                        }
                    )
                }
                Screen.Favorites -> {
                    FavoritesScreen(
                        channels = channels,
                        favoriteIds = favoriteIds,
                        onChannelClick = { ch ->
                            activePlayingChannelId = ch.id
                        }
                    )
                }
                Screen.Search -> {
                    SearchScreen(
                        channels = channels,
                        onChannelClick = { ch ->
                            activePlayingChannelId = ch.id
                        }
                    )
                }
                Screen.Settings -> {
                    SettingsScreen(
                        repository = streamSourceRepository,
                        totalChannelsCount = channels.size
                    )
                }
                is Screen.Player -> {
                    // Handled above
                }
            }
        }
    }
}
