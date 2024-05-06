plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") //启用注解器kapt
}

android {
    namespace = "com.illidancstormrage.test"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.illidancstormrage.test"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
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
}

dependencies {

    implementation(project(":utils"))
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
//    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")

    // 协程 - 支持安卓
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    val lifecycleVersion = "2.6.2"
    // Lifecycles only (without ViewModel or LiveData) 生命周期感知组件
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    // LiveData 组件
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}