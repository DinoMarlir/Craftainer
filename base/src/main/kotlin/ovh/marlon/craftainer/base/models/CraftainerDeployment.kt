package ovh.marlon.craftainer.base.models

import com.github.dockerjava.api.model.Image
import kotlinx.serialization.Serializable
import ovh.marlon.craftainer.base.utils.Constants.CRAFTAINER_NETWORK_NAME
import ovh.marlon.craftainer.sdk.impl.CraftainerClient
import ovh.marlon.craftainer.sdk.impl.resources.ContainerImpl
import ovh.marlon.craftainer.sdk.resources.Container
import kotlin.jvm.optionals.getOrNull

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
data class CraftainerDeployment(
    val name: String,
    val image: String,
    val env: Map<String, String>? = null,
    val ports: Map<Int, Int>? = null,
    val network: String? = CRAFTAINER_NETWORK_NAME,
    val labels: Map<String, String>? = mapOf(
        "craftainer-resource" to "true",
        "craftainer-deployment" to name,
    ),
    val minReplicas: Int = 1,
    val maxReplicas: Int = 1
) {
    fun deploy(client: CraftainerClient): List<Container<com.github.dockerjava.api.model.Container, Image>> {

        if (minReplicas.coerceAtMost(maxReplicas) < 1) {
            return emptyList()
        }

        for (i in 1..minReplicas) {
            val containerName = if (minReplicas > 1) "$name-$i" else name
            if (client.getContainer(containerName).isPresent) {
                continue
            }
            container(name, client)
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
        volumes = emptyMap(),
    )
}