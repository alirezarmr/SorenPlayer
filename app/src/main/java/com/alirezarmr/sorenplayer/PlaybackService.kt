package com.alirezarmr.sorenplayer

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

/**
 * سرویس اصلی پخش Media در Soren Player.
 *
 * Player در این Service قرار دارد تا:
 * - پخش در پس‌زمینه ادامه داشته باشد.
 * - Activity مستقیماً مالک Player نباشد.
 * - Android Auto بتواند از MediaSession استفاده کند.
 */
class PlaybackService : MediaLibraryService() {

    // Player اصلی برنامه
    private lateinit var player: ExoPlayer

    // MediaLibrarySession برای ارتباط با Android Auto و MediaController
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        // ایجاد ExoPlayer
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()

        // Intent برای باز کردن Activity اصلی
        val sessionActivityIntent = Intent(this, MainActivity::class.java)

        // PendingIntent مربوط به Activity
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // ایجاد MediaLibrarySession
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            LibrarySessionCallback()
        )
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    /**
     * Callback مربوط به Media Library.
     *
     * در مراحل بعدی:
     * - Music
     * - Videos
     * - Folders
     * - Search
     * را به این قسمت اضافه می‌کنیم.
     */
    private class LibrarySessionCallback : MediaLibrarySession.Callback

    /**
     * برگرداندن Session برای MediaController
     * و Android Auto.
     */
    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaLibrarySession {
        return mediaSession
    }

    override fun onDestroy() {

        // آزاد کردن MediaSession
        mediaSession.release()

        // آزاد کردن Player
        player.release()

        super.onDestroy()
    }
}
