package com.derppening.derplib.dependency

import com.derppening.gradle.DependencyModule
import org.gradle.api.artifacts.dsl.DependencyHandler

object JetbrainsAnnotations : DependencyModule("org.jetbrains", "annotations", "24.0.1")

fun DependencyHandler.`jetbrains-annotations`(module: String? = null, version: String? = null): Any =
    JetbrainsAnnotations.asDependencyNotation(module, version)
