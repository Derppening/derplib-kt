package com.derppening.derplib.dependency

import com.derppening.gradle.DependencyModule
import org.gradle.api.artifacts.dsl.DependencyHandler

object Logback : DependencyModule("ch.qos.logback", "logback", "1.4.8")

fun DependencyHandler.logback(module: String? = null, version: String? = null): Any =
    Logback.asDependencyNotation(module, version)
