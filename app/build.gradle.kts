plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.review.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.review.app"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-alpha"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":review-android"))
    implementation(project(":review-material3"))
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.core:core-ktx:1.19.0")
}
