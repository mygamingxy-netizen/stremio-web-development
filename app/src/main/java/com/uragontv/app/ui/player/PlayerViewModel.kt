package com.uragontv.app.ui.player

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import com.uragontv.app.data.model.AspectRatioMode
import com.uragontv.app.data.model.BufferProfile
import com.uragontv.app.data.model.Channel
import com.uragontv.app.data.repository.FavoritesRepository
import com.uragontv.app.data.repository.StreamSourceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@UnstableApi
class PlayerViewModel(
    private val context: Context,
    private val streamSourceRepository: StreamSourceRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private var exoPlayer: ExoPlayer? = null

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val _allChannels = MutableStateFlow<List<Channel>>(emptyList())
    val allChannels: StateFlow<List<Channel>> = _allChannels.asStateFlow()

    private val _isControlsVisible = MutableStateFlow(true)
    val isControlsVisible: StateFlow<Boolean> = _isControlsVisible.asStateFlow()

    private val _isQuickPickerVisible = MutableStateFlow(false)
    val isQuickPickerVisible: StateFlow<Boolean> = _isQuickPickerVisible.asStateFlow()

    private val _isBuffering = MutableStateFlow(true)
    val isBuffering: StateFlow<Boolean> = _isBuffering.asStateFlow()

    private val _isReconnecting = MutableStateFlow(false)
    val isReconnecting: StateFlow<Boolean> = _isReconnecting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _aspectRatioMode = MutableStateFlow(AspectRatioMode.FIT)
    val aspectRatioMode: StateFlow<AspectRatioMode> = _aspectRatioMode.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _streamResolution = MutableStateFlow("1080p")
    val streamResolution: StateFlow<String> = _streamResolution.asStateFlow()

    private var controlsHideJob: Job? = null
    private var reconnectAttempts = 0

    init {
        _allChannels.value = streamSourceRepository.channels.value
    }

    fun getPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            initPlayer()
        }
        return exoPlayer!!
    }

    private fun initPlayer() {
        val bufferConfig = streamSourceRepository.bufferProfile.value
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                bufferConfig.minBufferMs,
                bufferConfig.maxBufferMs,
                1000,
                2000
            )
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build()
            .apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _isBuffering.value = true
                                _errorMessage.value = null
                            }
                            Player.STATE_READY -> {
                                _isBuffering.value = false
                                _isReconnecting.value = false
                                _errorMessage.value = null
                                reconnectAttempts = 0
                            }
                            Player.STATE_ENDED -> {
                                _isBuffering.value = false
                            }
                            Player.STATE_IDLE -> {
                                _isBuffering.value = false
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("UragonPlayer", "Player error: ${error.message}", error)
                        _isBuffering.value = false
                        handleStreamError(error)
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        // Extract resolution from video track
                        for (group in tracks.groups) {
                            if (group.type == C.TRACK_TYPE_VIDEO) {
                                for (i in 0 until group.length) {
                                    val format = group.getTrackFormat(i)
                                    if (format.height > 0) {
                                        _streamResolution.value = "${format.width}x${format.height}"
                                    }
                                }
                            }
                        }
                    }
                })
            }
    }

    fun playChannel(channelId: String) {
        val channel = streamSourceRepository.getChannelById(channelId)
            ?: _allChannels.value.find { it.id == channelId }
            ?: return

        _currentChannel.value = channel
        _isFavorite.value = favoritesRepository.isFavorite(channel.id)
        _streamResolution.value = channel.quality
        _errorMessage.value = null
        _isBuffering.value = true

        val player = getPlayer()
        player.stop()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("UragonTv/1.0 (Android TV ExoPlayer)")
            .setConnectTimeoutMs(8000)
            .setReadTimeoutMs(10000)
            .setAllowCrossProtocolRedirects(true)

        val uri = Uri.parse(channel.streamUrl)
        val mediaSource = createMediaSource(uri, dataSourceFactory)

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()

        resetControlsTimer()
    }

    private fun createMediaSource(
        uri: Uri,
        dataSourceFactory: DefaultHttpDataSource.Factory
    ): MediaSource {
        val urlString = uri.toString().lowercase()
        return if (urlString.contains(".m3u8")) {
            HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(MediaItem.fromUri(uri))
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
        }
    }

    private fun handleStreamError(error: PlaybackException) {
        val autoReconnect = streamSourceRepository.autoReconnect.value
        if (autoReconnect && reconnectAttempts < 5) {
            reconnectAttempts++
            _isReconnecting.value = true
            _errorMessage.value = "Signal lost. Reconnecting (Attempt $reconnectAttempts/5)..."

            viewModelScope.launch {
                delay(2000L * reconnectAttempts)
                _currentChannel.value?.let { ch ->
                    playChannel(ch.id)
                }
            }
        } else {
            _errorMessage.value = "Unable to load stream. Please check connection or try another channel."
            showControlsPermanently()
        }
    }

    fun retryPlayback() {
        reconnectAttempts = 0
        _currentChannel.value?.let {
            playChannel(it.id)
        }
    }

    fun toggleFavorite() {
        val channel = _currentChannel.value ?: return
        favoritesRepository.toggleFavorite(channel.id)
        _isFavorite.value = favoritesRepository.isFavorite(channel.id)
    }

    fun cycleAspectRatio() {
        _aspectRatioMode.value = when (_aspectRatioMode.value) {
            AspectRatioMode.FIT -> AspectRatioMode.ZOOM
            AspectRatioMode.ZOOM -> AspectRatioMode.STRETCH
            AspectRatioMode.STRETCH -> AspectRatioMode.FIT
        }
        resetControlsTimer()
    }

    fun toggleQuickPicker() {
        _isQuickPickerVisible.value = !_isQuickPickerVisible.value
        if (_isQuickPickerVisible.value) {
            _isControlsVisible.value = true
            controlsHideJob?.cancel()
        } else {
            resetControlsTimer()
        }
    }

    fun showControls() {
        _isControlsVisible.value = true
        resetControlsTimer()
    }

    fun hideControls() {
        _isControlsVisible.value = false
        _isQuickPickerVisible.value = false
        controlsHideJob?.cancel()
    }

    fun toggleControls() {
        if (_isControlsVisible.value) {
            hideControls()
        } else {
            showControls()
        }
    }

    fun resetControlsTimer() {
        _isControlsVisible.value = true
        controlsHideJob?.cancel()
        controlsHideJob = viewModelScope.launch {
            delay(5000)
            if (!_isQuickPickerVisible.value) {
                _isControlsVisible.value = false
            }
        }
    }

    private fun showControlsPermanently() {
        _isControlsVisible.value = true
        controlsHideJob?.cancel()
    }

    fun playNextChannel() {
        val channels = _allChannels.value
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == _currentChannel.value?.id }
        val nextIndex = (currentIndex + 1) % channels.size
        playChannel(channels[nextIndex].id)
    }

    fun playPreviousChannel() {
        val channels = _allChannels.value
        if (channels.isEmpty()) return
        val currentIndex = channels.indexOfFirst { it.id == _currentChannel.value?.id }
        val prevIndex = if (currentIndex <= 0) channels.size - 1 else currentIndex - 1
        playChannel(channels[prevIndex].id)
    }

    fun release() {
        controlsHideJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}
