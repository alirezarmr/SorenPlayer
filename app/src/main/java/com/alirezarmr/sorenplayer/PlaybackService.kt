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
 * این سرویس مسئول ارتباط بین ExoPlayer،
 * MediaSession و Android Auto است.
 *
 * در مراحل بعدی کتابخانه فایل‌ها را به این سرویس اضافه می‌کنیم.
 */
class PlaybackService : MediaLibraryService() {

    // پخش‌کننده اصلی Media3
    private lateinit var player: ExoPlayer

    // نشست Media برای ارتباط با Android Auto و کنترلرهای Media
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()

        // ایجاد Player اصلی برنامه
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()

        // Intent برای باز کردن صفحه اصلی برنامه
        val sessionActivityIntent = Intent(this, MainActivity::class.java)

        // PendingIntent مربوط به Activity اصلی
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
     * فعلاً فقط ساختار پایه را ایجاد می‌کنیم.
     * در مراحل بعدی Browse و Search را اضافه خواهیم کرد.
     */
    private class LibrarySessionCallback : MediaLibrarySession.Callback

    /**
     * Android Auto از این Session برای کنترل Player استفاده می‌کند.
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
