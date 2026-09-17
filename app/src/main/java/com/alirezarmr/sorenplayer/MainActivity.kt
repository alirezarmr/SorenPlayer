package com.alirezarmr.sorenplayer

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView

/**
 * صفحه اصلی Soren Player.
 *
 * این Activity فقط رابط کاربری و کنترل MediaController را مدیریت می‌کند.
 *
 * Player اصلی داخل PlaybackService قرار دارد.
 */
class MainActivity : AppCompatActivity() {

    // MediaController برای ارتباط با PlaybackService
    private var mediaController: MediaController? = null

    // نمایش نام فایل فعلی
    private lateinit var currentMediaText: TextView

    // نمایش وضعیت Player
    private lateinit var statusText: TextView

    // دکمه Play / Pause
    private lateinit var playButton: Button

    // PlayerView برای نمایش ویدیو
    private lateinit var playerView: PlayerView

    // کد انتخاب فایل
    private val filePickerRequestCode = 300

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ساخت رابط کاربری
        createUserInterface()

        // اتصال Activity به PlaybackService
        connectToPlaybackService()
    }

    /**
     * ساخت رابط کاربری برنامه.
     */
    private fun createUserInterface() {

        // صفحه اصلی
        val rootLayout = LinearLayout(this)

        rootLayout.orientation = LinearLayout.VERTICAL

        rootLayout.setPadding(28, 28, 28, 28)

        rootLayout.setBackgroundColor(
            Color.rgb(15, 23, 42)
        )

        // عنوان برنامه
        val titleText = TextView(this)

        titleText.text = "Soren Player"
        titleText.textSize = 30f
        titleText.setTextColor(Color.WHITE)
        titleText.gravity = Gravity.CENTER

        rootLayout.addView(
            titleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        // زیرعنوان
        val subtitleText = TextView(this)

        subtitleText.text = "Local Media Player"
        subtitleText.textSize = 14f
        subtitleText.setTextColor(Color.LTGRAY)
        subtitleText.gravity = Gravity.CENTER

        rootLayout.addView(
            subtitleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                40
            )
        )

        // فاصله
        addSpace(rootLayout, 15)

        // دکمه Music
        val musicButton = createMainButton("Music")

        musicButton.setOnClickListener {

            // انتخاب فایل صوتی
            openFilePicker(
                arrayOf(
                    "audio/mpeg",
                    "audio/*"
                )
            )
        }

        rootLayout.addView(musicButton)

        // دکمه Videos
        val videoButton = createMainButton("Videos")

        videoButton.setOnClickListener {

            // انتخاب فایل ویدیویی
            openFilePicker(
                arrayOf(
                    "video/mp4",
                    "video/x-matroska",
                    "video/*"
                )
            )
        }

        rootLayout.addView(videoButton)

        // فاصله
        addSpace(rootLayout, 18)

        // عنوان Now Playing
        val nowPlayingTitle = TextView(this)

        nowPlayingTitle.text = "NOW PLAYING"
        nowPlayingTitle.textSize = 16f
        nowPlayingTitle.setTextColor(Color.WHITE)
        nowPlayingTitle.gravity = Gravity.CENTER

        rootLayout.addView(
            nowPlayingTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // PlayerView
        playerView = PlayerView(this)

        playerView.useController = false

        playerView.visibility = View.GONE

        val playerBackground = GradientDrawable()

        playerBackground.setColor(
            Color.rgb(2, 6, 23)
        )

        playerBackground.cornerRadius = 20f

        playerView.background = playerBackground

        rootLayout.addView(
            playerView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                190
            )
        )

        // نام فایل
        currentMediaText = TextView(this)

        currentMediaText.text = "No media selected"
        currentMediaText.textSize = 17f
        currentMediaText.setTextColor(Color.WHITE)
        currentMediaText.gravity = Gravity.CENTER

        rootLayout.addView(
            currentMediaText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        // وضعیت
        statusText = TextView(this)

        statusText.text = "Ready"
        statusText.textSize = 13f
        statusText.setTextColor(Color.LTGRAY)
        statusText.gravity = Gravity.CENTER

        rootLayout.addView(
            statusText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                30
            )
        )

        // فاصله
        addSpace(rootLayout, 10)

        // کنترل‌های Player
        val controlsLayout = LinearLayout(this)

        controlsLayout.orientation = LinearLayout.HORIZONTAL
        controlsLayout.gravity = Gravity.CENTER

        // دکمه Previous
        val previousButton = createControlButton("Previous")

        previousButton.setOnClickListener {

            mediaController?.seekToPreviousMediaItem()
        }

        controlsLayout.addView(
            previousButton,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        // دکمه Play
        playButton = createControlButton("Play")

        playButton.setOnClickListener {

            if (mediaController?.isPlaying == true) {

                mediaController?.pause()

            } else {

                mediaController?.play()
            }

            updatePlayButton()
        }

        controlsLayout.addView(
            playButton,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        // دکمه Next
        val nextButton = createControlButton("Next")

        nextButton.setOnClickListener {

            mediaController?.seekToNextMediaItem()
        }

        controlsLayout.addView(
            nextButton,
            LinearLayout.LayoutParams(
                0,
                60,
                1f
            )
        )

        rootLayout.addView(
            controlsLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // فاصله
        addSpace(rootLayout, 12)

        // دکمه Folders
        val foldersButton = createMainButton("Folders")

        foldersButton.setOnClickListener {

            statusText.text = "Folder browser will be added next"
        }

        rootLayout.addView(foldersButton)

        // نمایش صفحه
        setContentView(rootLayout)
    }

    /**
     * اتصال Activity به PlaybackService.
     */
    private fun connectToPlaybackService() {

        // مشخص کردن Service مربوط به برنامه
        val sessionToken = SessionToken(
            this,
            ComponentName(
                this,
                PlaybackService::class.java
            )
        )

        // ایجاد MediaController
        val controllerFuture =
            MediaController.Builder(
                this,
                sessionToken
            ).buildAsync()

        // دریافت نتیجه اتصال
        controllerFuture.addListener({

            try {

                // دریافت MediaController
                mediaController = controllerFuture.get()

                // اتصال PlayerView به MediaController
                playerView.player = mediaController

                statusText.text = "Player ready"

            } catch (exception: Exception) {

                statusText.text = "Player connection failed"
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * باز کردن File Picker.
     */
    private fun openFilePicker(
        mimeTypes: Array<String>
    ) {

        // ایجاد Intent انتخاب فایل
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // فقط فایل‌های قابل باز شدن
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // استفاده از MIME Type
        intent.type = "*/*"

        // مشخص کردن انواع فایل
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            mimeTypes
        )

        // اجرای File Picker
        startActivityForResult(
            intent,
            filePickerRequestCode
        )
    }

    /**
     * دریافت فایل انتخاب‌شده.
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        // بررسی نتیجه انتخاب فایل
        if (
            requestCode == filePickerRequestCode &&
            resultCode == RESULT_OK &&
            data?.data != null
        ) {

            // دریافت URI فایل
            val fileUri = data.data!!

            // دریافت نام فایل
            val fileName = getFileName(fileUri)

            // نمایش نام فایل
            currentMediaText.text = fileName

            // ارسال فایل به Media3
            playMedia(fileUri, fileName)
        }
    }

    /**
     * ارسال فایل انتخاب‌شده به Media3.
     */
    private fun playMedia(
        uri: Uri,
        fileName: String
    ) {

        // بررسی آماده بودن Controller
        val controller = mediaController

        if (controller == null) {

            statusText.text = "Player is not ready"

            return
        }

        // ساخت MediaItem
        val mediaItem = MediaItem.fromUri(uri)

        // قرار دادن فایل داخل Player
        controller.setMediaItem(mediaItem)

        // آماده‌سازی Player
        controller.prepare()

        // شروع پخش
        controller.play()

        // نمایش وضعیت
        statusText.text = "Playing"

        // تغییر دکمه
        playButton.text = "Pause"

        // نمایش PlayerView برای فایل ویدیویی
        if (
            fileName.endsWith(
                ".mp4",
                ignoreCase = true
            ) ||
            fileName.endsWith(
                ".mkv",
                ignoreCase = true
            )
        ) {

            playerView.visibility = View.VISIBLE

        } else {

            playerView.visibility = View.GONE
        }
    }

    /**
     * دریافت نام فایل از URI.
     */
    private fun getFileName(uri: Uri): String {

        // جستجوی اطلاعات فایل
        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            // پیدا کردن ستون نام فایل
            val nameIndex =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            // خواندن نام فایل
            if (
                nameIndex >= 0 &&
                it.moveToFirst()
            ) {

                return it.getString(nameIndex)
            }
        }

        // نام جایگزین
        return uri.lastPathSegment ?: "Unknown file"
    }

    /**
     * ساخت دکمه اصلی برنامه.
     */
    private fun createMainButton(
        text: String
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 15f

        return button
    }

    /**
     * ساخت دکمه‌های کنترل Player.
     */
    private fun createControlButton(
        text: String
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 13f

        return button
    }

    /**
     * ایجاد فضای خالی بین بخش‌ها.
     */
    private fun addSpace(
        layout: LinearLayout,
        height: Int
    ) {

        val space = TextView(this)

        layout.addView(
            space,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
            )
        )
    }

    /**
     * به‌روزرسانی متن دکمه Play / Pause.
     */
    private fun updatePlayButton() {

        if (mediaController?.isPlaying == true) {

            playButton.text = "Pause"

            statusText.text = "Playing"

        } else {

            playButton.text = "Play"

            statusText.text = "Paused"
        }
    }

    override fun onDestroy() {

        // آزاد کردن MediaController
        mediaController?.release()

        mediaController = null

        super.onDestroy()
    }
}
