plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // 프로젝트에 맞춰 17로 설정
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

}
