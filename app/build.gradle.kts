plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") //启用注解器kapt
    id("androidx.navigation.safeargs.kotlin") //navigation 插件 - 这是kts代替classpath写法
    kotlin("plugin.serialization") //启用全局项目配置 序列化插件
}

android {
    namespace = "com.illidancstormrage.cstormmemo"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.illidancstormrage.cstormmemo"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(project(":utils"))
    implementation(project(":csrich"))

    //hutool
    implementation("cn.hutool:hutool-all:5.8.16")
    //XXPermissions
    implementation("com.github.getActivity:XXPermissions:16.0") //权限库

    //room
    val roomVersion = "2.5.2"
    //AndroidX Room 运行时库
    implementation("androidx.room:room-runtime:$roomVersion")
    // To use Kotlin annotation processing tool 		(kapt)  kotlin版
    kapt("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")

    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    // 协程 - 支持安卓
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    val lifecycleVersion = "2.6.2"
    // Lifecycles only (without ViewModel or LiveData) 生命周期感知组件
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // LiveData 组件
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit json 转换器
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // retrofit转换器的okhttp3依赖
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    //media3-exoplayer
    val media3Version = "1.1.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //导航
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}