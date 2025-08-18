plugins {
    id("com.gradleup.shadow") version "9.0.2"
}

dependencies {
    api(project(":sdk-impl"))
    api(project(":shared"))
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