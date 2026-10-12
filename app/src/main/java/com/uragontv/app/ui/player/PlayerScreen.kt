package com.uragontv.app.ui.player

import android.app.Activity
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.uragontv.app.data.model.AspectRatioMode

@UnstableApi
@Composable
fun PlayerScreen(
    channelId: String,
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentChannel by viewModel.currentChannel.collectAsState()
    val isControlsVisible by viewModel.isControlsVisible.collectAsState()
    val isQuickPickerVisible by viewModel.isQuickPickerVisible.collectAsState()
    val isBuffering by viewModel.isBuffering.collectAsState()
    val isReconnecting by viewModel.isReconnecting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val aspectRatioMode by viewModel.aspectRatioMode.collectAsState()
    val streamResolution by viewModel.streamResolution.collectAsState()
    val allChannels by viewModel.allChannels.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    // Start playback on channelId change
    LaunchedEffect(channelId) {
        viewModel.playChannel(channelId)
    }

    // Keep screen on while playing on TV
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        focusRequester.requestFocus()

        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            viewModel.release()
        }
    }

    // Back handling
    BackHandler {
        if (isQuickPickerVisible) {
            viewModel.toggleQuickPicker()
        } else if (isControlsVisible) {
            viewModel.hideControls()
        } else {
            onBackClick()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                viewModel.toggleControls()
            }
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER,
                        KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                            if (!isControlsVisible) {
                                viewModel.showControls()
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (!isControlsVisible) {
                                viewModel.playPreviousChannel()
                                viewModel.showControls()
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (!isControlsVisible) {
                                viewModel.playNextChannel()
                                viewModel.showControls()
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT,
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            viewModel.showControls()
                            false
                        }
                        else -> {
                            viewModel.resetControlsTimer()
                            false
                        }
                    }
                } else {
                    false
                }
            }
    ) {
        // Media3 ExoPlayer Video Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    player = viewModel.getPlayer()
                    resizeMode = when (aspectRatioMode) {
                        AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        AspectRatioMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                }
            },
            update = { playerView ->
                playerView.player = viewModel.getPlayer()
                playerView.resizeMode = when (aspectRatioMode) {
                    AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    AspectRatioMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay with OSD Controls, Buffering state, Quick Channel Picker
        PlayerControlsOverlay(
            channel = currentChannel,
            isVisible = isControlsVisible,
            isBuffering = isBuffering,
            isReconnecting = isReconnecting,
            errorMessage = errorMessage,
            isFavorite = isFavorite,
            aspectRatioMode = aspectRatioMode,
            streamResolution = streamResolution,
            isQuickPickerVisible = isQuickPickerVisible,
            allChannels = allChannels,
            onBackClick = onBackClick,
            onToggleFavorite = { viewModel.toggleFavorite() },
            onCycleAspectRatio = { viewModel.cycleAspectRatio() },
            onToggleQuickPicker = { viewModel.toggleQuickPicker() },
            onChannelSelected = { ch ->
                viewModel.playChannel(ch.id)
                viewModel.toggleQuickPicker()
            },
            onPreviousChannel = { viewModel.playPreviousChannel() },
            onNextChannel = { viewModel.playNextChannel() },
            onRetry = { viewModel.retryPlayback() }
        )
    }
}
