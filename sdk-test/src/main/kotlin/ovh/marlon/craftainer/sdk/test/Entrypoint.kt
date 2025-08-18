package ovh.marlon.craftainer.sdk.test

import ovh.marlon.craftainer.sdk.impl.CraftainerClient

fun main() {
    val client = CraftainerClient.createUsingSocket()

    println("Driver available: ${client.isDriverAvailable()}")
}