package ovh.marlon.craftainer.sdk.test

import kotlinx.coroutines.runBlocking
import ovh.marlon.craftainer.sdk.impl.CraftainerClient
import ovh.marlon.craftainer.sdk.resources.Network

fun main() = runBlocking {
    val client = CraftainerClient.createUsingSocket()

    println(Network.NetworkDriver.BRIDGE.toString().lowercase())

    client.createNetwork("test-network", Network.NetworkDriver.BRIDGE, "10.0.0.0/24", "10.0.0.1")

    client.getNetworks().forEach {
        println("Netzwerk: ${it.name} (${it.id})")
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