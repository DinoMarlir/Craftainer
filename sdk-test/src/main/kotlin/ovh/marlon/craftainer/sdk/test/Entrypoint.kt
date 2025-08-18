package ovh.marlon.craftainer.sdk.test

import ovh.marlon.craftainer.sdk.impl.CraftainerClient

fun main() {
    val client = CraftainerClient.createUsingSocket()

    client.getVolume("test_volume_v2").get().remove()

    println("Volume count: ${client.getVolumes().size}")
    client.getVolumes().forEach {
        println("- ${it.name}")
    }
}