pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("androidx.navigation.safeargs") version "2.7.7"
        id("org.jetbrains.kotlin.jvm") version "1.9.23"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
        id("com.google.dagger.hilt.android") version "2.48"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("app/libs")
        }
        flatDir {
            dirs("wear/libs")
        }
    }
}

rootProject.name = "NOGOROK"
include(":app")
include(":wear")
include(":common")
