plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("dependencies") {
            id = "com.derppening.derplib.dependencies"
            implementationClass = "com.derppening.derplib.DependenciesPlugin"
        }
    }
}
