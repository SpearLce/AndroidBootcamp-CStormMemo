plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") //启用注解器kapt

    //发布插件
    `maven-publish` // 插件必须的
}

//发布配置
publishing { //发布配置
    publications {
        register<MavenPublication>("release") {
            groupId = "com.illidancstormrage.StormrageToolkit"
            artifactId = "utils" //表示项目具体的构件ID，即产品或模块的名字。
            version = "1.0" //义化版本控制规范，如major.minor.patch（主版本.次版本.修订版本）
            afterEvaluate {
                from(components["release"]) // 安卓release —— "java" - jar
            }
        }
    }
}



android {
    namespace = "com.illidancstormrage.utils"
    compileSdk = 33

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
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

    //room
    val roomVersion = "2.5.2"
    //AndroidX Room 运行时库
    implementation("androidx.room:room-runtime:$roomVersion")
    // To use Kotlin annotation processing tool  (kapt)  kotlin版
    kapt("androidx.room:room-compiler:$roomVersion")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")


    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(kotlin("reflect"))
}