plugins {
    // پلاگین ساخت برنامه اندروید
    id("com.android.application")

    // پلاگین Kotlin برای کدنویسی اندروید
    id("org.jetbrains.kotlin.android")
}

android {
    // نام پکیج برنامه
    namespace = "com.alirezarmr.sorenplayer"

    // نسخه SDK مورد استفاده برای Build
    compileSdk = 36

    defaultConfig {
        // شناسه یکتای برنامه
        applicationId = "com.alirezarmr.sorenplayer"

        // حداقل نسخه اندرویدی که برنامه روی آن اجرا می‌شود
        minSdk = 23

        // نسخه SDK هدف
        targetSdk = 36

        // شماره نسخه داخلی برنامه
        versionCode = 1

        // نسخه‌ای که به کاربر نمایش داده می‌شود
        versionName = "1.0"

        // تست‌های پیش‌فرض اندروید
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // فعلاً برای ساده‌تر شدن Build، Release را بدون Minify می‌سازیم
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // استفاده از Java 17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // نسخه JVM مورد استفاده Kotlin
        jvmTarget = "17"
    }
}

dependencies {

    // کتابخانه اصلی AndroidX
    implementation("androidx.core:core-ktx:1.17.0")

    // اجزای سازگاری رابط کاربری
    implementation("androidx.appcompat:appcompat:1.7.1")

    // Activity مدرن اندروید
    implementation("androidx.activity:activity-ktx:1.10.1")

    // Media3 / ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.8.0")

    // رابط MediaSession برای کنترل پخش
    implementation("androidx.media3:media3-session:1.8.0")

    // رابط کاربری Player
    implementation("androidx.media3:media3-ui:1.8.0")
}
