package ovh.marlon.craftainer.sdk.test

import kotlinx.coroutines.runBlocking
import ovh.marlon.craftainer.sdk.impl.CraftainerClient
import ovh.marlon.craftainer.sdk.resources.Port

fun main(): kotlin.Unit = runBlocking {
    val client = CraftainerClient.createUsingSocket()

    val container = client.createContainer(
        image = "nginx:latest",
        name = "mein-nginx",
        ports = listOf(Port(host = 8080, container = 80)),
        environment = emptyMap(),
        volumes = mapOf(
            "/home/user/nginx/html" to "/usr/share/nginx/html"
        ),
        networks = emptyList(),
        command = null
    )

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