plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(project(":sdk-impl"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveFileName = "adapter-velocity.jar"
    }

    build {
        dependsOn(shadowJar)
    }
}