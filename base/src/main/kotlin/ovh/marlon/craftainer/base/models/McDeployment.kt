package ovh.marlon.craftainer.base.models

import com.github.dockerjava.api.model.Image
import kotlinx.serialization.Serializable
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_NETWORK_NAME
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_RESOURCE_LABEL
import ovh.marlon.craftainer.sdk.impl.CraftainerClient
import ovh.marlon.craftainer.sdk.resources.Container

/**
 * A deployment configuration for a Craftainer service.
 *
 * @param name The name of the deployment.
 * @param image The Docker image to use for the deployment.
 * @param env A map of environment variables to set in the container.
 * @param ports A map of port mappings from host to container.
 * @param network The Docker network to connect the container to.
 * @param labels A map of labels to apply to the container.
 */
@Serializable
data class McDeployment(
    val name: String,
    val image: String,
    val env: Map<String, String>? = null,
    val ports: Map<Int, Int>? = null,
    val network: String? = CRAFTAINER_NETWORK_NAME,
    val labels: Map<String, String>? = mapOf(
        CRAFTAINER_RESOURCE_LABEL to "true",
        "craftainer-deployment" to name,
    ),
    val minReplicas: Int = 1,
    val maxReplicas: Int = 1,
    val mounts: Map<String, String> = emptyMap()
) {
    fun deploy(client: CraftainerClient): List<Container<com.github.dockerjava.api.model.Container, Image>> {
        if (minReplicas > maxReplicas) {
            return emptyList()
        }

        if (availableReplicas(client) < minReplicas) {
            while (availableReplicas(client) < minReplicas) {
                fun nextFreeNumber(): Int {
                    val existingNumbers = client.getContainers().mapNotNull {
                        if (it.name == name) {
                            0
                        } else if (it.name?.startsWith("$name-") == true) {
                            it.name?.substringAfter("$name-")?.toIntOrNull()
                        } else {
                            null
                        }
                    }.toSet()
                    var i = 0
                    while (true) {
                        if (!existingNumbers.contains(i)) {
                            return i
                        }
                        i++
                    }
                }

                val containerName = "$name-${nextFreeNumber()}"

                container(containerName, client)
            }
        }

        // Create the container
        return client.getContainers().filter {
            it.name == name || it.name?.startsWith("$name-") == true
        }.map { it }
    }

    fun container(name: String, client: CraftainerClient) = client.createContainer(
        image = image,
        name = name,
        environment = env ?: emptyMap(),
        ports = ports?.map { (host, container) ->
            ovh.marlon.craftainer.sdk.resources.Port(host, container)
        } ?: emptyList(),
        networks = if (network != null) {
            listOfNotNull(client.getNetworkByName(network).orElseThrow())
        } else {
            emptyList()
        },
        command = "/usr/bin/run-bungeecord.sh",
        volumes = mounts,
        labels = labels ?: emptyMap()
    )

    fun availableReplicas(client: CraftainerClient): Int {
        return client.getContainers().count {
            (it.name == name || it.name?.startsWith("$name-") == true) && (it.status == Container.ContainerStatus.HEALTHY || it.status == Container.ContainerStatus.STARTING) && it.labels["craftainer-deployment"] == name
        }
    }
}