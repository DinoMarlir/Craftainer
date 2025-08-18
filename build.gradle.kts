plugins {
    kotlin("jvm") version "2.2.10"
}

allprojects {
    group = "ovh.marlon.craftainer"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}