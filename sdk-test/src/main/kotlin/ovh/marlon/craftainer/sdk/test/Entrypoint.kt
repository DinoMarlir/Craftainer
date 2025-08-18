package ovh.marlon.craftainer.sdk.test

import kotlinx.coroutines.runBlocking
import ovh.marlon.craftainer.sdk.impl.CraftainerClient

fun main() = runBlocking {
    val client = CraftainerClient.createUsingSocket()

    // print available images
    client.getImages().forEach {
        println("Image: ${it.repository}:${it.tag} (Registry: ${it.registry})")
    }

}

fun anotherTest(client: CraftainerClient) = runBlocking {
    println("Starte Pull von nginx:latest ...")

    val success = client.pullImage("https://registry-1.docker.io", "nginx", "latest")

    if (success) {
        println("✅ Image erfolgreich geladen!")
    } else {
        println("❌ Fehler beim Laden des Images.")
    }

    println("Programm beendet.")
}