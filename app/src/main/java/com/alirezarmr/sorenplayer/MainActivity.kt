package com.alirezarmr.sorenplayer

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * صفحه اصلی برنامه Soren Player
 *
 * این Activity فعلاً فقط یک صفحه ساده نمایش می‌دهد.
 * در مراحل بعدی:
 * - انتخاب فایل
 * - لیست موسیقی
 * - لیست ویدیو
 * - کنترل Player
 * را به آن اضافه می‌کنیم.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ساخت TextView برای نمایش پیام اولیه برنامه
        val textView = TextView(this)

        // متن رابط کاربری برنامه انگلیسی است
        textView.text = "Soren Player"

        // اندازه متن
        textView.textSize = 28f

        // قرار دادن TextView به عنوان صفحه اصلی
        setContentView(textView)
    }
}
