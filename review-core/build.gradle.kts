
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])
            artifactId = "review-core"
        }
    }
}
