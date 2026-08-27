plugins {
    id("com.android.library")
}

android {
    namespace = "io.github.review.material3"
    compileSdk = 37

    defaultConfig {
        minSdk = 21
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
    api(project(":review-core"))
    api(project(":review-android"))
    api("com.google.android.material:material:1.14.0")
}
