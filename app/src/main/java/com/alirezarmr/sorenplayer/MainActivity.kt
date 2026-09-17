package com.alirezarmr.sorenplayer

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * صفحه اصلی برنامه Soren Player
 *
 * در این مرحله رابط کاربری اصلی برنامه ساخته می‌شود.
 *
 * بخش‌های فعلی:
 * - Music
 * - Videos
 * - Folders
 * - Now Playing
 * - کنترل‌های Previous / Play / Next
 *
 * در مراحل بعدی این قسمت‌ها به Media3 و فایل‌های واقعی متصل می‌شوند.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ایجاد صفحه اصلی
        val rootLayout = LinearLayout(this)

        // تنظیم جهت قرارگیری عناصر به صورت عمودی
        rootLayout.orientation = LinearLayout.VERTICAL

        // ایجاد فاصله داخلی صفحه
        rootLayout.setPadding(32, 32, 32, 32)

        // تنظیم پس‌زمینه تیره
        rootLayout.setBackgroundColor(Color.rgb(15, 23, 42))

        // عنوان برنامه
        val titleText = TextView(this)

        titleText.text = "Soren Player"
        titleText.textSize = 30f
        titleText.setTextColor(Color.WHITE)
        titleText.gravity = Gravity.CENTER

        // اضافه کردن عنوان به صفحه
        rootLayout.addView(
            titleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // فاصله بعد از عنوان
        val titleSpace = TextView(this)
        rootLayout.addView(
            titleSpace,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // دکمه Music
        val musicButton = Button(this)

        musicButton.text = "Music"

        // اضافه کردن دکمه Music
        rootLayout.addView(
            musicButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // دکمه Videos
        val videosButton = Button(this)

        videosButton.text = "Videos"

        // اضافه کردن دکمه Videos
        rootLayout.addView(
            videosButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // دکمه Folders
        val foldersButton = Button(this)

        foldersButton.text = "Folders"

        // اضافه کردن دکمه Folders
        rootLayout.addView(
            foldersButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // ایجاد فضای خالی برای جدا کردن Library از Player
        val playerSpace = TextView(this)

        rootLayout.addView(
            playerSpace,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // عنوان بخش پخش فعلی
        val nowPlayingTitle = TextView(this)

        nowPlayingTitle.text = "Now Playing"
        nowPlayingTitle.textSize = 20f
        nowPlayingTitle.setTextColor(Color.WHITE)
        nowPlayingTitle.gravity = Gravity.CENTER

        rootLayout.addView(
            nowPlayingTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // نام فایل در حال پخش
        val currentMediaText = TextView(this)

        currentMediaText.text = "No media selected"
        currentMediaText.textSize = 16f
        currentMediaText.setTextColor(Color.LTGRAY)
        currentMediaText.gravity = Gravity.CENTER

        rootLayout.addView(
            currentMediaText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            )
        )

        // ایجاد Layout برای کنترل‌های Player
        val controlsLayout = LinearLayout(this)

        controlsLayout.orientation = LinearLayout.HORIZONTAL
        controlsLayout.gravity = Gravity.CENTER

        // دکمه Previous
        val previousButton = Button(this)

        previousButton.text = "Previous"

        controlsLayout.addView(
            previousButton,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        // دکمه Play
        val playButton = Button(this)

        playButton.text = "Play"

        controlsLayout.addView(
            playButton,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        // دکمه Next
        val nextButton = Button(this)

        nextButton.text = "Next"

        controlsLayout.addView(
            nextButton,
            LinearLayout.LayoutParams(
                0,
                65,
                1f
            )
        )

        // اضافه کردن کنترل‌ها به صفحه
        rootLayout.addView(
            controlsLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // تنظیم صفحه اصلی برنامه
        setContentView(rootLayout)
    }
}
