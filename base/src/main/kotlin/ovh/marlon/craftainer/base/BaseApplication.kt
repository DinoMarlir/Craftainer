package ovh.marlon.craftainer.base

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.info
import ovh.marlon.craftainer.app.base.AbstractAppBase
import ovh.marlon.craftainer.base.models.CraftainerDeployment
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_NETWORK_NAME
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_RESOURCE_LABEL
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_VOLUME_MOUNT_PATH
import ovh.marlon.craftainer.sdk.impl.CraftainerClient
import ovh.marlon.craftainer.sdk.impl.resources.ContainerImpl
import ovh.marlon.craftainer.sdk.resources.Container
import java.io.File

class BaseApplication: AbstractAppBase("Base") {

    companion object {
        val client = CraftainerClient.createUsingSocket()
        val deployments = arrayListOf<CraftainerDeployment>()
        val craftainers = emptyList<ContainerImpl>()
    }

    override fun start() {
        if (!client.isDriverAvailable()) {
            terminal.info("Docker driver is not available. Please ensure you are running this inside a Docker container.")
            return
        }

        if (client.getNetworkByName(CRAFTAINER_NETWORK_NAME).isEmpty) {
            terminal.info("Craftainer network not found. Creating 'craftainer_network'...")
            client.createNetwork(CRAFTAINER_NETWORK_NAME)
            terminal.info("Network '$CRAFTAINER_NETWORK_NAME' created.")
        } else {
            terminal.info("Craftainer network already exists. Skipping creation.")
        }

        reattach()
        deleteStoppedContainers()
        resolveDeployments()
        deployAll()
    }

    private fun reattach() {
        terminal.info("Reattaching to existing Craftainer containers...")
        client.getContainers().filter { it.native().labels.containsKey(CRAFTAINER_RESOURCE_LABEL) }.forEach {
            terminal.info("${TextColors.gray("•")} Reattaching to container ${TextColors.brightGreen(it.name ?: "unknown")} (${TextColors.brightCyan(it.id)})")
            craftainers + it
        }
    }

    private fun deleteStoppedContainers() {
        terminal.info("Deleting stopped Craftainer containers...")
        craftainers.filter { it.native().labels.containsKey(CRAFTAINER_RESOURCE_LABEL) && it.status == Container.ContainerStatus.EXITED }.forEach {
            terminal.info("${TextColors.gray("•")} Deleting container ${TextColors.brightGreen(it.name ?: "unknown")} (${TextColors.brightCyan(it.id)})")
            client.deleteContainer(it.id)
        }
    }

    private fun resolveDeployments() {
        terminal.info("Resolving deployments...")
        val folder = File(CRAFTAINER_VOLUME_MOUNT_PATH, "deployments").let { if (!it.exists()) it.mkdirs(); it }

        folder.listFiles { file -> file.extension == "json" }?.forEach { file ->
            terminal.info("${TextColors.gray("•")} Found deployment file: ${TextColors.brightGreen(file.name)}")
            try {
                val deployment = CraftainerDeployment.serializer().let { serializer ->
                    ovh.marlon.craftainer.base.utils.Constants.JSON.decodeFromString(
                        serializer,
                        file.readText()
                    )
                }
                deployments.add(deployment)
                terminal.info("${TextColors.gray("  -")} Loaded deployment: ${TextColors.brightGreen(deployment.name)}")
            } catch (e: Exception) {
                terminal.info("${TextColors.red("  !")} Failed to load deployment from file ${TextColors.brightRed(file.name)}: ${e.message}")
            }
        }
    }

    private fun deployAll() {
        terminal.info("Deploying ${deployments.size} deployments...")
        deployments.forEach { deployment ->
            val deployedContainers = deployment.deploy(client)
            deployedContainers.forEach {
                terminal.info("${TextColors.gray("•")} Deployed container ${TextColors.brightGreen(it.name ?: "unknown")} (${TextColors.brightCyan(it.id)}) from deployment ${TextColors.brightGreen(deployment.name)}")
                craftainers + it as ContainerImpl
            }
        }
    }
}