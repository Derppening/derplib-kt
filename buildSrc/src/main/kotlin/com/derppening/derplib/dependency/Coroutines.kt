package com.derppening.derplib.dependency

import com.derppening.gradle.DependencyModule
import org.gradle.api.artifacts.dsl.DependencyHandler

object Coroutines : DependencyModule("org.jetbrains.kotlinx", "kotlinx-coroutines", "1.7.1")

fun DependencyHandler.coroutines(module: String? = null, version: String? = null): Any =
    Coroutines.asDependencyNotation(module, version)
