plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.derppening.derplib.dependencies")
}

allprojects {
    repositories {
        // Use Maven Central for resolving dependencies.
        mavenCentral()
    }
}

tasks {
    wrapper {
        gradleVersion = "8.2.1"
    }
}
