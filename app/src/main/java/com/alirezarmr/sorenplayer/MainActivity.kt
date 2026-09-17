package com.alirezarmr.sorenplayer

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView

/**
 * صفحه اصلی Soren Player.
 *
 * در این مرحله:
 * - Media Library
 * - انتخاب چند فایل
 * - Playlist
 * - Next / Previous
 * - رابط کاربری جدید
 * اضافه می‌شود.
 *
 * Player اصلی همچنان داخل PlaybackService قرار دارد.
 */
class MainActivity : AppCompatActivity() {

    // MediaController برای ارتباط با PlaybackService
    private var mediaController: MediaController? = null

    // نمایش نام فایل فعلی
    private lateinit var currentMediaText: TextView

    // نمایش وضعیت Player
    private lateinit var statusText: TextView

    // نمایش زمان پخش
    private lateinit var positionText: TextView

    // نوار وضعیت ساده پخش
    private lateinit var progressText: TextView

    // PlayerView برای نمایش ویدیو
    private lateinit var playerView: PlayerView

    // محل نمایش Library
    private lateinit var libraryLayout: LinearLayout

    // لیست فایل‌های Music
    private val musicItems = mutableListOf<MediaItem>()

    // لیست فایل‌های Video
    private val videoItems = mutableListOf<MediaItem>()

    // درخواست انتخاب فایل
    private val filePickerRequestCode = 500

    // Listener مربوط به تغییرات Player
    private var playerListener: Player.Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ساخت رابط کاربری
        createUserInterface()

        // اتصال به PlaybackService
        connectToPlaybackService()
    }

    /**
     * ساخت رابط کاربری اصلی برنامه.
     */
    private fun createUserInterface() {

        // صفحه اصلی
        val rootLayout = LinearLayout(this)

        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setPadding(24, 24, 24, 24)

        rootLayout.setBackgroundColor(
            Color.rgb(15, 23, 42)
        )

        // عنوان برنامه
        val titleText = TextView(this)

        titleText.text = "Soren Player"
        titleText.textSize = 30f
        titleText.setTextColor(Color.WHITE)
        titleText.gravity = Gravity.CENTER
        titleText.setTypeface(null, Typeface.BOLD)

        rootLayout.addView(
            titleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                48
            )
        )

        // زیرعنوان
        val subtitleText = TextView(this)

        subtitleText.text = "LOCAL MEDIA PLAYER"
        subtitleText.textSize = 12f
        subtitleText.setTextColor(Color.LTGRAY)
        subtitleText.gravity = Gravity.CENTER

        rootLayout.addView(
            subtitleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                30
            )
        )

        addSpace(rootLayout, 12)

        // نوار دکمه‌های Library
        val categoryScroll = HorizontalScrollView(this)

        categoryScroll.isHorizontalScrollBarEnabled = false

        val categoryLayout = LinearLayout(this)

        categoryLayout.orientation = LinearLayout.HORIZONTAL

        // دکمه Music
        val musicButton = createCategoryButton("Music")

        musicButton.setOnClickListener {

            openFilePicker(
                arrayOf(
                    "audio/mpeg",
                    "audio/*"
                )
            )
        }

        categoryLayout.addView(musicButton)

        // دکمه Videos
        val videoButton = createCategoryButton("Videos")

        videoButton.setOnClickListener {

            openFilePicker(
                arrayOf(
                    "video/mp4",
                    "video/x-matroska",
                    "video/*"
                )
            )
        }

        categoryLayout.addView(videoButton)

        // دکمه Play All
        val playAllButton = createCategoryButton("Play All")

        playAllButton.setOnClickListener {

            playAllMedia()
        }

        categoryLayout.addView(playAllButton)

        categoryScroll.addView(categoryLayout)

        rootLayout.addView(
            categoryScroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            )
        )

        addSpace(rootLayout, 10)

        // عنوان Library
        val libraryTitle = createSectionTitle("MEDIA LIBRARY")

        rootLayout.addView(libraryTitle)

        // ScrollView برای Library
        val libraryScroll = ScrollView(this)

        libraryLayout = LinearLayout(this)

        libraryLayout.orientation = LinearLayout.VERTICAL

        libraryScroll.addView(libraryLayout)

        rootLayout.addView(
            libraryScroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        addSpace(rootLayout, 10)

        // عنوان Now Playing
        val nowPlayingTitle =
            createSectionTitle("NOW PLAYING")

        rootLayout.addView(nowPlayingTitle)

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
                170
            )
        )

        // نام فایل
        currentMediaText = TextView(this)

        currentMediaText.text = "No media selected"
        currentMediaText.textSize = 16f
        currentMediaText.setTextColor(Color.WHITE)
        currentMediaText.gravity = Gravity.CENTER

        rootLayout.addView(
            currentMediaText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                42
            )
        )

        // نوار پیشرفت متنی
        progressText = TextView(this)

        progressText.text = "00:00 ───────────────── 00:00"
        progressText.textSize = 12f
        progressText.setTextColor(Color.LTGRAY)
        progressText.gravity = Gravity.CENTER

        rootLayout.addView(
            progressText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                30
            )
        )

        // وضعیت
        statusText = TextView(this)

        statusText.text = "Ready"
        statusText.textSize = 12f
        statusText.setTextColor(Color.LTGRAY)
        statusText.gravity = Gravity.CENTER

        rootLayout.addView(
            statusText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                28
            )
        )

        // کنترل‌های Player
        val controlsLayout = LinearLayout(this)

        controlsLayout.orientation = LinearLayout.HORIZONTAL
        controlsLayout.gravity = Gravity.CENTER

        // Previous
        val previousButton =
            createControlButton("Previous")

        previousButton.setOnClickListener {

            mediaController?.seekToPreviousMediaItem()
        }

        controlsLayout.addView(
            previousButton,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        // Play / Pause
        val playButton =
            createControlButton("Play")

        playButton.setOnClickListener {

            val controller = mediaController

            if (controller == null) {
                return@setOnClickListener
            }

            if (controller.isPlaying) {

                controller.pause()

            } else {

                controller.play()
            }

            updatePlayerState()
        }

        controlsLayout.addView(
            playButton,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        // Next
        val nextButton =
            createControlButton("Next")

        nextButton.setOnClickListener {

            mediaController?.seekToNextMediaItem()
        }

        controlsLayout.addView(
            nextButton,
            LinearLayout.LayoutParams(
                0,
                55,
                1f
            )
        )

        rootLayout.addView(
            controlsLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            )
        )

        // نمایش صفحه
        setContentView(rootLayout)

        // Library اولیه
        refreshLibrary()
    }

    /**
     * اتصال Activity به PlaybackService.
     */
    private fun connectToPlaybackService() {

        val sessionToken = SessionToken(
            this,
            ComponentName(
                this,
                PlaybackService::class.java
            )
        )

        val controllerFuture =
            MediaController.Builder(
                this,
                sessionToken
            ).buildAsync()

        controllerFuture.addListener({

            try {

                // دریافت MediaController
                mediaController =
                    controllerFuture.get()

                // اتصال PlayerView
                playerView.player =
                    mediaController

                // ایجاد Listener
                playerListener =
                    object : Player.Listener {

                        override fun onMediaItemTransition(
                            mediaItem: MediaItem?,
                            reason: Int
                        ) {

                            updatePlayerState()
                        }

                        override fun onIsPlayingChanged(
                            isPlaying: Boolean
                        ) {

                            updatePlayerState()
                        }

                        override fun onPlaybackStateChanged(
                            playbackState: Int
                        ) {

                            updatePlayerState()
                        }
                    }

                // اضافه کردن Listener
                mediaController?.addListener(
                    playerListener!!
                )

                statusText.text = "Player ready"

            } catch (exception: Exception) {

                statusText.text =
                    "Player connection failed"
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * باز کردن File Picker برای انتخاب چند فایل.
     */
    private fun openFilePicker(
        mimeTypes: Array<String>
    ) {

        val intent =
            Intent(Intent.ACTION_OPEN_DOCUMENT)

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        intent.type = "*/*"

        // اجازه انتخاب چند فایل
        intent.putExtra(
            Intent.EXTRA_ALLOW_MULTIPLE,
            true
        )

        // مشخص کردن نوع فایل‌ها
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            mimeTypes
        )

        startActivityForResult(
            intent,
            filePickerRequestCode
        )
    }

    /**
     * دریافت نتیجه File Picker.
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

        if (
            requestCode != filePickerRequestCode ||
            resultCode != RESULT_OK ||
            data == null
        ) {
            return
        }

        // لیست URI های انتخاب‌شده
        val selectedUris =
            mutableListOf<Uri>()

        // بررسی انتخاب چند فایل
        val clipData = data.clipData

        if (clipData != null) {

            for (index in 0 until clipData.itemCount) {

                selectedUris.add(
                    clipData.getItemAt(index).uri
                )
            }

        } else if (data.data != null) {

            // اگر فقط یک فایل انتخاب شده باشد
            selectedUris.add(
                data.data!!
            )
        }

        // اضافه کردن فایل‌ها به Library
        for (uri in selectedUris) {

            addMediaToLibrary(uri)
        }

        // به‌روزرسانی Library
        refreshLibrary()
    }

    /**
     * اضافه کردن یک فایل به Library.
     */
    private fun addMediaToLibrary(
        uri: Uri
    ) {

        // دریافت نام فایل
        val fileName =
            getFileName(uri)

        // ایجاد MediaItem
        val mediaItem =
            MediaItem.Builder()
                .setMediaId(uri.toString())
                .setUri(uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(fileName)
                        .build()
                )
                .build()

        // تشخیص فایل صوتی
        if (
            fileName.endsWith(
                ".mp3",
                ignoreCase = true
            )
        ) {

            // جلوگیری از اضافه شدن تکراری
            if (
                musicItems.none {
                    it.mediaId == mediaItem.mediaId
                }
            ) {

                musicItems.add(mediaItem)
            }

            return
        }

        // تشخیص فایل ویدیویی
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

            // جلوگیری از اضافه شدن تکراری
            if (
                videoItems.none {
                    it.mediaId == mediaItem.mediaId
                }
            ) {

                videoItems.add(mediaItem)
            }
        }
    }

    /**
     * بازسازی نمایش Library.
     */
    private fun refreshLibrary() {

        // پاک کردن لیست قبلی
        libraryLayout.removeAllViews()

        // نمایش Music
        if (musicItems.isNotEmpty()) {

            libraryLayout.addView(
                createLibraryHeader(
                    "MUSIC"
                )
            )

            for (item in musicItems) {

                libraryLayout.addView(
                    createMediaRow(
                        item,
                        false
                    )
                )
            }
        }

        // نمایش Videos
        if (videoItems.isNotEmpty()) {

            libraryLayout.addView(
                createLibraryHeader(
                    "VIDEOS"
                )
            )

            for (item in videoItems) {

                libraryLayout.addView(
                    createMediaRow(
                        item,
                        true
                    )
                )
            }
        }

        // اگر Library خالی باشد
        if (
            musicItems.isEmpty() &&
            videoItems.isEmpty()
        ) {

            val emptyText = TextView(this)

            emptyText.text =
                "No media files added yet"

            emptyText.textSize = 15f
            emptyText.setTextColor(Color.LTGRAY)
            emptyText.gravity = Gravity.CENTER
            emptyText.setPadding(
                10,
                30,
                10,
                30
            )

            libraryLayout.addView(
                emptyText
            )
        }
    }

    /**
     * ساخت عنوان Music / Videos.
     */
    private fun createLibraryHeader(
        title: String
    ): TextView {

        val text = TextView(this)

        text.text = title
        text.textSize = 13f
        text.setTextColor(Color.LTGRAY)
        text.setTypeface(
            null,
            Typeface.BOLD
        )

        text.setPadding(
            12,
            12,
            12,
            6
        )

        return text
    }

    /**
     * ساخت ردیف Media Library.
     */
    private fun createMediaRow(
        item: MediaItem,
        isVideo: Boolean
    ): LinearLayout {

        // ردیف اصلی
        val row = LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            12,
            4,
            8,
            4
        )

        // نام فایل
        val nameText = TextView(this)

        nameText.text =
            item.mediaMetadata.title
                ?: "Unknown media"

        nameText.textSize = 14f
        nameText.setTextColor(Color.WHITE)

        row.addView(
            nameText,
            LinearLayout.LayoutParams(
                0,
                52,
                1f
            )
        )

        // دکمه Play
        val playButton =
            createControlButton("Play")

        playButton.setOnClickListener {

            playSingleItem(item)
        }

        row.addView(
            playButton,
            LinearLayout.LayoutParams(
                95,
                52
            )
        )

        return row
    }

    /**
     * پخش یک فایل.
     */
    private fun playSingleItem(
        item: MediaItem
    ) {

        val controller =
            mediaController
                ?: return

        // قرار دادن فقط این فایل در Playlist
        controller.setMediaItem(item)

        // آماده‌سازی
        controller.prepare()

        // شروع پخش
        controller.play()

        // نمایش نام فایل
        currentMediaText.text =
            item.mediaMetadata.title
                ?: "Unknown media"

        // نمایش PlayerView برای ویدیو
        val name =
            item.mediaMetadata.title
                ?.toString()
                ?: ""

        playerView.visibility =
            if (
                name.endsWith(
                    ".mp4",
                    true
                ) ||
                name.endsWith(
                    ".mkv",
                    true
                )
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }

        updatePlayerState()
    }

    /**
     * پخش کل Library به عنوان Playlist.
     */
    private fun playAllMedia() {

        val controller =
            mediaController
                ?: return

        // ساخت لیست کامل Media
        val allItems =
            mutableListOf<MediaItem>()

        allItems.addAll(
            musicItems
        )

        allItems.addAll(
            videoItems
        )

        // اگر Library خالی است
        if (allItems.isEmpty()) {

            statusText.text =
                "Library is empty"

            return
        }

        // قرار دادن کل Library در Playlist
        controller.setMediaItems(
            allItems,
            0,
            0L
        )

        // آماده‌سازی
        controller.prepare()

        // شروع پخش
        controller.play()

        updatePlayerState()
    }

    /**
     * به‌روزرسانی اطلاعات Player.
     */
    private fun updatePlayerState() {

        val controller =
            mediaController
                ?: return

        // MediaItem فعلی
        val currentItem =
            controller.currentMediaItem

        if (currentItem != null) {

            currentMediaText.text =
                currentItem.mediaMetadata.title
                    ?: "Unknown media"
        }

        // وضعیت پخش
        statusText.text =
            when {

                controller.isPlaying ->
                    "Playing"

                controller.playbackState ==
                    Player.STATE_BUFFERING ->
                    "Buffering"

                controller.playbackState ==
                    Player.STATE_ENDED ->
                    "Finished"

                else ->
                    "Paused"
            }

        // زمان فعلی
        val position =
            controller.currentPosition

        val duration =
            controller.duration

        progressText.text =
            "${formatTime(position)} ──────────────── ${formatTime(duration)}"
    }

    /**
     * تبدیل میلی‌ثانیه به MM:SS.
     */
    private fun formatTime(
        milliseconds: Long
    ): String {

        if (milliseconds < 0) {
            return "00:00"
        }

        val totalSeconds =
            milliseconds / 1000

        val minutes =
            totalSeconds / 60

        val seconds =
            totalSeconds % 60

        return String.format(
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    /**
     * دریافت نام فایل از URI.
     */
    private fun getFileName(
        uri: Uri
    ): String {

        val cursor =
            contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (
                nameIndex >= 0 &&
                it.moveToFirst()
            ) {

                return it.getString(
                    nameIndex
                )
            }
        }

        return uri.lastPathSegment
            ?: "Unknown file"
    }

    /**
     * ساخت دکمه دسته‌بندی.
     */
    private fun createCategoryButton(
        text: String
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 12f

        return button
    }

    /**
     * ساخت دکمه کنترل Player.
     */
    private fun createControlButton(
        text: String
    ): Button {

        val button = Button(this)

        button.text = text
        button.textSize = 11f

        return button
    }

    /**
     * ساخت عنوان بخش.
     */
    private fun createSectionTitle(
        text: String
    ): TextView {

        val title = TextView(this)

        title.text = text
        title.textSize = 14f
        title.setTextColor(Color.WHITE)
        title.setTypeface(
            null,
            Typeface.BOLD
        )
        title.gravity = Gravity.CENTER

        return title
    }

    /**
     * ایجاد فضای خالی.
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

    override fun onDestroy() {

        // حذف Listener
        if (playerListener != null) {

            mediaController?.removeListener(
                playerListener!!
            )
        }

        // آزاد کردن Controller
        mediaController?.release()

        mediaController = null

        super.onDestroy()
    }
}
