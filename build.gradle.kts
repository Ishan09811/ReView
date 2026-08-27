plugins {
    id("com.android.application") version "9.3.2" apply false
    id("com.android.library") version "9.3.2" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.10" apply false
    id("maven-publish")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

val publishVersion: String = System.getenv("VERSION") ?: "local"

subprojects {
    group = "io.github.review"
    version = publishVersion
}