plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    // 컴파일용 패키지명(코드 패키지). appId와 달라도 됩니다.
    namespace = "com.example.nogorokwear"
    compileSdk = 35

    defaultConfig {
        // ★ 폰과 동일해야 데이터 레이어 통신 가능
        applicationId = "com.example.nogorok"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        // debug에 applicationIdSuffix가 있으면 제거하세요.
        debug {
            // applicationIdSuffix ".debug"  // 사용하지 마세요 (폰/워치 appId가 달라짐)
        }
    }

    compileOptions {
        // Kotlin 2.x + 최신 Compose와 맞춰 17 권장
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Samsung Health Sensor API (워치 측)
    implementation(files("libs/samsung-health-sensor-api-v1.3.0.aar"))

    // Wear Data Layer
    implementation("com.google.android.gms:play-services-wearable:18.2.0")

    // Wear Compose
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")

    // Compose (버전 카탈로그 사용)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)

    // 필요 시 스플래시
    implementation(libs.androidx.core.splashscreen)

    // 테스트/디버그
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
