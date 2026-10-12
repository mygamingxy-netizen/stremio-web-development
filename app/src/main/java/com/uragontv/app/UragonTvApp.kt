package com.uragontv.app

import android.app.Application
import com.uragontv.app.data.repository.FavoritesRepository
import com.uragontv.app.data.repository.StreamSourceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UragonTvApp : Application() {

    lateinit var favoritesRepository: FavoritesRepository
        private set

    lateinit var streamSourceRepository: StreamSourceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        favoritesRepository = FavoritesRepository.getInstance(this)
        streamSourceRepository = StreamSourceRepository.getInstance(this)

        // Preload default channels on app launch
        CoroutineScope(Dispatchers.IO).launch {
            streamSourceRepository.loadChannels()
        }
    }
}
