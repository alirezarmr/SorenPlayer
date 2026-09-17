package com.alirezarmr.sorenplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * صفحه اصلی برنامه Soren Player
 *
 * در این مرحله:
 * - انتخاب فایل Music
 * - انتخاب فایل Video
 * - نمایش نام فایل انتخاب‌شده
 * را اضافه می‌کنیم.
 *
 * برای دسترسی به فایل‌ها از Android Storage Access Framework
 * استفاده می‌کنیم و نیازی به مجوز مستقیم Storage نداریم.
 */
class MainActivity : AppCompatActivity() {

    // کد درخواست انتخاب فایل Music
    private val musicPickerRequestCode = 100

    // کد درخواست انتخاب فایل Video
    private val videoPickerRequestCode = 200

    // نمایش نام فایل انتخاب‌شده
    private lateinit var currentMediaText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ایجاد صفحه اصلی
        val rootLayout = LinearLayout(this)

        // قرار دادن عناصر به صورت عمودی
        rootLayout.orientation = LinearLayout.VERTICAL

        // فاصله داخلی صفحه
        rootLayout.setPadding(32, 32, 32, 32)

        // عنوان برنامه
        val titleText = TextView(this)

        titleText.text = "Soren Player"
        titleText.textSize = 30f
        titleText.setTextColor(android.graphics.Color.WHITE)
        titleText.gravity = Gravity.CENTER

        rootLayout.addView(
            titleText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // فاصله زیر عنوان
        val titleSpace = TextView(this)

        rootLayout.addView(
            titleSpace,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // دکمه انتخاب Music
        val musicButton = Button(this)

        musicButton.text = "Music"

        musicButton.setOnClickListener {

            // باز کردن File Picker برای فایل‌های صوتی
            openFilePicker(
                arrayOf(
                    "audio/mpeg",
                    "audio/*"
                ),
                musicPickerRequestCode
            )
        }

        rootLayout.addView(
            musicButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // دکمه انتخاب Videos
        val videosButton = Button(this)

        videosButton.text = "Videos"

        videosButton.setOnClickListener {

            // باز کردن File Picker برای فایل‌های ویدیویی
            openFilePicker(
                arrayOf(
                    "video/mp4",
                    "video/*"
                ),
                videoPickerRequestCode
            )
        }

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

        foldersButton.setOnClickListener {

            // فعلاً Folder Browser را در مرحله بعد اضافه می‌کنیم
            currentMediaText.text = "Folder browser will be added next"
        }

        rootLayout.addView(
            foldersButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                65
            )
        )

        // فاصله قبل از Now Playing
        val playerSpace = TextView(this)

        rootLayout.addView(
            playerSpace,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // عنوان Now Playing
        val nowPlayingTitle = TextView(this)

        nowPlayingTitle.text = "Now Playing"
        nowPlayingTitle.textSize = 20f
        nowPlayingTitle.setTextColor(android.graphics.Color.WHITE)
        nowPlayingTitle.gravity = Gravity.CENTER

        rootLayout.addView(
            nowPlayingTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // نمایش فایل انتخاب‌شده
        currentMediaText = TextView(this)

        currentMediaText.text = "No media selected"
        currentMediaText.textSize = 16f
        currentMediaText.setTextColor(android.graphics.Color.LTGRAY)
        currentMediaText.gravity = Gravity.CENTER

        rootLayout.addView(
            currentMediaText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                70
            )
        )

        // تنظیم صفحه اصلی
        setContentView(rootLayout)
    }

    /**
     * باز کردن File Picker اندروید
     *
     * کاربر می‌تواند فایل موردنظر را از حافظه گوشی
     * یا فایل‌های قابل دسترس انتخاب کند.
     */
    private fun openFilePicker(
        mimeTypes: Array<String>,
        requestCode: Int
    ) {

        // ایجاد Intent برای انتخاب فایل
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // فقط فایل‌های قابل خواندن نمایش داده شوند
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // اجازه انتخاب چند نوع MIME
        intent.type = "*/*"

        // مشخص کردن MIME Type های مجاز
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        // اجرای File Picker
        startActivityForResult(intent, requestCode)
    }

    /**
     * دریافت نتیجه File Picker
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        // بررسی اینکه کاربر یک فایل انتخاب کرده است
        if (resultCode == RESULT_OK && data?.data != null) {

            // دریافت URI فایل
            val fileUri: Uri = data.data!!

            // استخراج نام فایل
            val fileName = getFileName(fileUri)

            // نمایش نام فایل
            currentMediaText.text = fileName
        }
    }

    /**
     * استخراج نام فایل از URI
     */
    private fun getFileName(uri: Uri): String {

        // تلاش برای دریافت نام واقعی فایل
        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            val nameIndex =
                it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)

            if (nameIndex >= 0 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }

        // اگر نام فایل قابل دریافت نبود
        return uri.lastPathSegment ?: "Unknown file"
    }
}
