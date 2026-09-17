package com.alirezarmr.sorenplayer

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

/**
 * سرویس اصلی پخش Media در Soren Player
 *
 * این سرویس هسته ارتباط برنامه با Media3 و Android Auto است.
 *
 * در مراحل بعدی:
 * - فایل‌های MP3 را به Library اضافه می‌کنیم.
 * - فایل‌های MP4 و MKV را اضافه می‌کنیم.
 * - پوشه‌ها را نمایش می‌دهیم.
 * - کنترل Play / Pause / Next / Previous را فعال می‌کنیم.
 */
class PlaybackService : MediaLibraryService() {

    // Player اصلی برنامه
    private lateinit var player: ExoPlayer

    // MediaSession برای ارتباط Player با Android Auto و سایر کنترلرها
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        // ساخت ExoPlayer
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()

        // ساخت PendingIntent برای باز کردن صفحه اصلی برنامه
        val sessionActivityIntent = Intent(this, MainActivity::class.java)

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // ساخت MediaLibrarySession
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            LibrarySessionCallback()
        )
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    /**
     * Callback مربوط به Media Library
     *
     * فعلاً ساختار اولیه را ایجاد می‌کنیم.
     * در مرحله بعد Library واقعی فایل‌ها را به این بخش اضافه خواهیم کرد.
     */
    private class LibrarySessionCallback :
        MediaLibrarySession.Callback {

        // در مراحل بعدی متدهای مربوط به Browse و Search
        // برای Android Auto را اینجا اضافه می‌کنیم.
    }

    /**
     * Android Auto و سایر Media Controller ها
     * از این Session برای کنترل پخش استفاده می‌کنند.
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
