package com.uragontv.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.uragontv.app.data.model.BufferProfile
import com.uragontv.app.data.model.Channel
import com.uragontv.app.data.parser.JsonChannelParser
import com.uragontv.app.data.parser.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class StreamSourceRepository(
    private val context: Context,
    private val favoritesRepository: FavoritesRepository
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("uragon_settings", Context.MODE_PRIVATE)

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sourceUrl = MutableStateFlow(
        prefs.getString(KEY_SOURCE_URL, "asset://sources.m3u") ?: "asset://sources.m3u"
    )
    val sourceUrl: StateFlow<String> = _sourceUrl.asStateFlow()

    private val _bufferProfile = MutableStateFlow(
        try {
            BufferProfile.valueOf(
                prefs.getString(KEY_BUFFER_PROFILE, BufferProfile.STANDARD.name)
                    ?: BufferProfile.STANDARD.name
            )
        } catch (_: Exception) {
            BufferProfile.STANDARD
        }
    )
    val bufferProfile: StateFlow<BufferProfile> = _bufferProfile.asStateFlow()

    private val _autoReconnect = MutableStateFlow(
        prefs.getBoolean(KEY_AUTO_RECONNECT, true)
    )
    val autoReconnect: StateFlow<Boolean> = _autoReconnect.asStateFlow()

    suspend fun loadChannels(customUrl: String? = null) {
        _isLoading.value = true
        withContext(Dispatchers.IO) {
            try {
                val urlToLoad = customUrl ?: _sourceUrl.value
                val loadedList = if (urlToLoad.startsWith("http://", ignoreCase = true) ||
                    urlToLoad.startsWith("https://", ignoreCase = true)
                ) {
                    loadFromRemote(urlToLoad)
                } else if (urlToLoad.endsWith(".json", ignoreCase = true)) {
                    loadFromAssetJson()
                } else {
                    loadFromAssetM3u()
                }

                val favIds = favoritesRepository.favoriteIds.value
                val updatedWithFavs = loadedList.map { ch ->
                    ch.copy(isFavorite = favIds.contains(ch.id))
                }

                _channels.value = if (updatedWithFavs.isNotEmpty()) {
                    updatedWithFavs
                } else {
                    loadFromAssetJson()
                }
            } catch (e: Exception) {
                Log.e("StreamSourceRepo", "Error loading channels, falling back to asset JSON", e)
                try {
                    _channels.value = loadFromAssetJson()
                } catch (fallbackError: Exception) {
                    Log.e("StreamSourceRepo", "Critical fallback error", fallbackError)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFromAssetM3u(): List<Channel> {
        return context.assets.open("sources.m3u").use { stream ->
            M3uParser.parse(stream)
        }
    }

    private fun loadFromAssetJson(): List<Channel> {
        return context.assets.open("channels.json").use { stream ->
            JsonChannelParser.parse(stream)
        }
    }

    private fun loadFromRemote(remoteUrl: String): List<Channel> {
        val url = URL(remoteUrl)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 8000
            readTimeout = 8000
            requestMethod = "GET"
            setRequestProperty("User-Agent", "UragonTv/1.0 (Android TV)")
        }

        return connection.inputStream.use { stream ->
            if (remoteUrl.endsWith(".json", ignoreCase = true)) {
                JsonChannelParser.parse(stream)
            } else {
                M3uParser.parse(stream)
            }
        }
    }

    fun setSourceUrl(newUrl: String) {
        _sourceUrl.value = newUrl
        prefs.edit().putString(KEY_SOURCE_URL, newUrl).apply()
    }

    fun setBufferProfile(profile: BufferProfile) {
        _bufferProfile.value = profile
        prefs.edit().putString(KEY_BUFFER_PROFILE, profile.name).apply()
    }

    fun setAutoReconnect(enabled: Boolean) {
        _autoReconnect.value = enabled
        prefs.edit().putBoolean(KEY_AUTO_RECONNECT, enabled).apply()
    }

    fun getChannelById(channelId: String): Channel? {
        return _channels.value.find { it.id == channelId }
    }

    companion object {
        private const val KEY_SOURCE_URL = "setting_source_url"
        private const val KEY_BUFFER_PROFILE = "setting_buffer_profile"
        private const val KEY_AUTO_RECONNECT = "setting_auto_reconnect"

        @Volatile
        private var instance: StreamSourceRepository? = null

        fun getInstance(context: Context): StreamSourceRepository {
            return instance ?: synchronized(this) {
                val favRepo = FavoritesRepository.getInstance(context)
                instance ?: StreamSourceRepository(context.applicationContext, favRepo).also {
                    instance = it
                }
            }
        }
    }
}
