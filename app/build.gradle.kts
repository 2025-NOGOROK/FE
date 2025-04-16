plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.nogorok"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nogorok"
        minSdk = 24
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
    }

    // ★★★ 꼭 이렇게 맞춰줘야 함! ★★★
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17   // 자바 소스코드 호환 버전
        targetCompatibility = JavaVersion.VERSION_17   // 자바 바이트코드 생성 버전
    }

    kotlinOptions {
        jvmTarget = "17"   // 코틀린도 JVM 17로 컴파일
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

}
