package com.aecosystem.lets_listen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aecosystem.let_listen.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var spotifyClient: SpotifyClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        spotifyClient.connectSpotifyAppRemote()
    }

    override fun onStop() {
        super.onStop()
        spotifyClient.disconnectSpotifyAppRemote()
    }
}