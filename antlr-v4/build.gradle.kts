import com.derppening.derplib.dependency.antlr4
import com.derppening.derplib.dependency.`jetbrains-annotations`
import com.derppening.derplib.dependency.`junit-jupiter`
import com.derppening.derplib.dependency.`junit-platform`

plugins {
    kotlin("jvm")
    `java-library`
    id("com.github.johnrengelman.shadow")
}

version = "0.1.0"

dependencies {
    api(antlr4())

    implementation(project(":util"))
    compileOnly(`jetbrains-annotations`())

    testCompileOnly("org.jetbrains:annotations:21.0.1")
    testImplementation(`junit-jupiter`())
    testImplementation(`junit-platform`("runner"))
    testRuntimeOnly(`junit-platform`("console"))
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.0")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}