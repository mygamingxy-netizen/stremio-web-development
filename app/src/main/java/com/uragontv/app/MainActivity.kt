package com.uragontv.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.uragontv.app.ui.navigation.TvNavHost
import com.uragontv.app.ui.player.PlayerViewModel
import com.uragontv.app.ui.theme.UragonTvTheme

@UnstableApi
class MainActivity : ComponentActivity() {

    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as UragonTvApp
        playerViewModel = PlayerViewModel(
            context = applicationContext,
            streamSourceRepository = app.streamSourceRepository,
            favoritesRepository = app.favoritesRepository
        )

        setContent {
            UragonTvTheme {
                TvNavHost(
                    streamSourceRepository = app.streamSourceRepository,
                    favoritesRepository = app.favoritesRepository,
                    playerViewModel = playerViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.release()
    }
}
