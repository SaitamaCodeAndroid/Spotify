package com.aecosystem.lets_listen

import android.content.Context
import com.aecosystem.lets_listen.utils.CLIENT_ID
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

private const val LOG = "connect"

class SpotifyClient constructor(
    @ApplicationContext val context: Context
) {
    private lateinit var spotifyAppRemote: SpotifyAppRemote
    private val connectionParams = ConnectionParams.Builder(CLIENT_ID)
        .showAuthView(true)
        .build()

    fun connectSpotifyAppRemote() {
        SpotifyAppRemote.connect(context, connectionParams, object : ConnectionListener {
            override fun onConnected(spar: SpotifyAppRemote) {
                Timber.tag(LOG).d("Connected! Yay!")
                spotifyAppRemote = spar
                playMusicList()
            }

            override fun onFailure(error: Throwable) {
                Timber.tag(LOG).e(error)
            }

        })
    }

    fun playMusicList() {
        spotifyAppRemote.playerApi.play("spotify:playlist:37i9dQZF1DX7K31D69s4M1")
    }

    fun disconnectSpotifyAppRemote() {
        SpotifyAppRemote.disconnect(spotifyAppRemote)
    }
}
