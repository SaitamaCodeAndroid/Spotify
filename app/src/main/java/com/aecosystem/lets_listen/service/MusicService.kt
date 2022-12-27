package com.aecosystem.lets_listen.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.aecosystem.lets_listen.components.NOTIFICATION_ID
import com.aecosystem.lets_listen.components.NotificationComponent
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

private const val LOG_TAG = "audio_service"
private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var notificationComponent: NotificationComponent

    var isForeGroundService = false

    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager.getLaunchIntentForPackage(packageName)
        PendingIntent.getActivity(this, 0, activityIntent, 0)

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())
            //setCallback(MySessionCallback())
            setSessionToken(sessionToken)
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlayer(exoPlayer)
        }

        notificationComponent = NotificationComponent(
            context = this,
            sessionToken = mediaSession.sessionToken,
            notificationListener = MusicPlayerNotificationListener(this)
        ) {

        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }
}

class MusicPlayerNotificationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
            isForeGroundService = false
            stopSelf()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if (ongoing && !isForeGroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForeGroundService = true
            }
        }
    }
}
