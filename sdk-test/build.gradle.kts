plugins {
    id("com.gradleup.shadow") version "9.0.2"
}

dependencies {
    api(project(":sdk-impl"))
}

tasks {
    shadowJar {
        archiveFileName = "craftainer-sdk-test.jar"

        manifest {
            attributes(
                mapOf(
                    "Main-Class" to "ovh.marlon.craftainer.sdk.test.EntrypointKt"
                )
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }
}