package com.derppening.derplib.dependency

import com.derppening.gradle.DependencyModule
import org.gradle.api.artifacts.dsl.DependencyHandler

object ANTLR4 : DependencyModule("org.antlr", "antlr4", "4.13.0")

fun DependencyHandler.antlr4(module: String? = null, version: String? = null): Any =
    ANTLR4.asDependencyNotation(module, version)
