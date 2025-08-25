plugins {
    id("com.gradleup.shadow") version "9.0.2"
    kotlin("plugin.serialization") version "2.2.0"
}

dependencies {
    api(project(":sdk-impl"))
    api(project(":shared"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}

tasks {
    shadowJar {
        archiveFileName = "base.jar"

        manifest {
            attributes(
                "Main-Class" to "ovh.marlon.craftainer.base.boot.BootstrapKt"
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }
}