/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 * For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.2.1/userguide/building_swift_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "derplib-kt"

require(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_1_8)) {
    "This library requires Java 8 or above."
}

include(
    "antlr-v4",
    "util"
)
