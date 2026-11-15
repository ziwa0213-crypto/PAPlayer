import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import java.util.TimeZone

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// ---- 版本号：YYMMDDHHmm，如 2611141033 ----
// versionCode 必须是 int，2611141033 超出上限(2147483647)，所以减 20 亿。
// 结果 611141033，跨年跨月都单调递增。
val generatedVersionName: String =
    providers.environmentVariable("PA_VERSION_NAME").orNull
        ?: SimpleDateFormat("yyMMddHHmm", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("Asia/Shanghai") }
            .format(Date())
val generatedVersionCode: Int = (generatedVersionName.toLong() - 2_000_000_000L).toInt()

// ---- 三把独立密钥，从 keystore/keystore.properties 读 ----
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore/keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "me.cwre.player"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.cwre.player"
        minSdk = 33
        targetSdk = 37
        versionCode = generatedVersionCode
        versionName = generatedVersionName
    }

    signingConfigs {
        fun add(name: String) {
            val fileName = keystoreProps.getProperty("$name.storeFile") ?: return
            val store = rootProject.file("keystore/$fileName")
            if (!store.exists()) return
            create("${name}Key") {
                storeFile = store
                storePassword = keystoreProps.getProperty("$name.storePassword")
                keyAlias = keystoreProps.getProperty("$name.keyAlias")
                keyPassword = keystoreProps.getProperty("$name.keyPassword")
            }
        }
        add("debug")
        add("release")
        add("vision")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = ""
            manifestPlaceholders["appLabel"] = "PA Player Debug"
            buildConfigField("String", "VARIANT", "\"debug\"")
            signingConfig = signingConfigs.findByName("debugKey")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            manifestPlaceholders["appLabel"] = "PA Player"
            buildConfigField("String", "VARIANT", "\"release\"")
            signingConfig = signingConfigs.findByName("releaseKey")
        }
        create("vision") {
            initWith(getByName("release"))
            applicationIdSuffix = ".vision"
            manifestPlaceholders["appLabel"] = "PA Player V"
            buildConfigField("String", "VARIANT", "\"vision\"")
            signingConfig = signingConfigs.findByName("visionKey")
            matchingFallbacks += listOf("release")
        }
    }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.activity.compose)
}
