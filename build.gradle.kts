// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false//kotlin序列化插件
    id("com.android.library") version "8.2.2" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.6.0" apply false //导航
}
