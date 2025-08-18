package ovh.marlon.craftainer.base

import com.github.ajalt.mordant.terminal.info
import ovh.marlon.craftainer.app.base.AbstractAppBase
import ovh.marlon.craftainer.sdk.impl.CraftainerClient

class BaseApplication: AbstractAppBase("Base") {

    companion object {
        val client = CraftainerClient.createUsingSocket()
    }

    override fun start() {
        if (!client.isDriverAvailable()) {
            terminal.info("Docker driver is not available. Please ensure you are running this inside a Docker container.")
            return
        }
    }
}