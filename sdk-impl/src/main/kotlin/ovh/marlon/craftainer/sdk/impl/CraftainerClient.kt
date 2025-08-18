package ovh.marlon.craftainer.sdk.impl

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.InspectVolumeResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Ports
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.impl.resources.ContainerImpl
import ovh.marlon.craftainer.sdk.impl.resources.ImageImpl
import ovh.marlon.craftainer.sdk.impl.resources.NetworkImpl
import ovh.marlon.craftainer.sdk.impl.resources.VolumeImpl
import ovh.marlon.craftainer.sdk.resources.*
import java.time.Duration
import java.util.*
import com.github.dockerjava.api.model.Container as NativeContainer
import com.github.dockerjava.api.model.Image as NativeImage
import com.github.dockerjava.api.model.Network as NativeNetwork
import com.github.dockerjava.api.model.Volume as NativeVolume

class CraftainerClient private constructor(config: DefaultDockerClientConfig): Craftainer<NativeContainer, NativeImage, InspectVolumeResponse, NativeNetwork>() {

    var httpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build()


    var client: DockerClient = DockerClientImpl.getInstance(config, httpClient)

    companion object {
        fun createUsingSocket(): CraftainerClient {
            val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build()
            return CraftainerClient(config)
        }
    }

    override fun isDriverAvailable(): Boolean {
        return try {
            client.pingCmd().exec()
            true
        } catch (_: Exception) {
            false
        }
    }

    override fun createContainer(
        image: String,
        name: String?,
        ports: List<Port>,
        environment: Map<String, String>,
        volumes: Map<String, String>, // hostPath → containerPath
        networks: List<Network<NativeNetwork>>,
        command: String?
    ): Container<NativeContainer, NativeImage> {

        // Container-Erstellungsbefehl vorbereiten
        val createCmd = client.createContainerCmd(image)
            .withName(name)
            .withEnv(environment.map { "${it.key}=${it.value}" })

        // Optionales Kommando setzen
        command?.let {
            createCmd.withCmd(*it.split(" ").toTypedArray())
        }

        // Ports konfigurieren
        val exposedPorts = ports.map { ExposedPort(it.container) }
        val portBindings = Ports().apply {
            ports.forEach { port ->
                bind(ExposedPort(port.container), Ports.Binding.bindPort(port.host))
            }
        }

        val hostConfig = HostConfig().withPortBindings(portBindings)

        // Volumes mounten (hostPath → containerPath)
        if (volumes.isNotEmpty()) {
            // Validierung (optional)
            volumes.forEach { (host, container) ->
                require(host.startsWith("/")) { "Host path must be absolute: $host" }
                require(container.startsWith("/")) { "Container path must be absolute: $container" }
            }

            val volumeDefs = volumes.values.map { NativeVolume.parse(it) }
            val binds = volumes.map { (hostPath, containerPath) ->
                com.github.dockerjava.api.model.Bind(hostPath, NativeVolume.parse(containerPath))
            }

            hostConfig.withBinds(binds)
            createCmd.withVolumes(volumeDefs)
        }

        // Netzwerk setzen (nur eines beim Erstellen möglich)
        networks.firstOrNull()?.let {
            createCmd.withNetworkMode(it.name)
        }

        // Host-Konfiguration anwenden
        createCmd.withHostConfig(hostConfig)

        // Container erstellen und starten
        val response = createCmd.exec()
        client.startContainerCmd(response.id).exec()

        // Container-Infos abrufen
        val containerInfo = client.inspectContainerCmd(response.id).exec()
        val nativeContainer = client.listContainersCmd()
            .withIdFilter(listOf(response.id))
            .exec()
            .firstOrNull() ?: error("Container not found")

        // Rückgabe als Container-Wrapper
        return ContainerImpl(containerInfo.id, nativeContainer, this)
    }

    override fun getContainer(id: String): Optional<Container<NativeContainer, NativeImage>> {
        return Optional.ofNullable(
            client.listContainersCmd()
                .withIdFilter(listOf(id))
                .exec()
                .firstOrNull()?.let { nativeContainer ->
                    ContainerImpl(
                        id = nativeContainer.id,
                        nativeContainer = nativeContainer,
                        craftainer = this
                    )
                }
        )
    }

    override fun getContainers(): List<Container<NativeContainer, NativeImage>> {
        return client.listContainersCmd()
            .withShowAll(true)
            .exec()
            .map { nativeContainer ->
                ContainerImpl(
                    id = nativeContainer.id,
                    nativeContainer = nativeContainer,
                    craftainer = this
                )
            }
    }

    override fun runContainer(id: String): Optional<Container<NativeContainer, NativeImage>> {
        return getContainer(id).map { container ->
            client.startContainerCmd(container.id).exec()
            container
        }
    }

    override fun stopContainer(id: String): Boolean {
        return try {
            client.stopContainerCmd(id).exec()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun restartContainer(id: String): Boolean {
        return try {
            client.restartContainerCmd(id).exec()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteContainer(id: String): Boolean {
        return try {
            client.removeContainerCmd(id)
                .withRemoveVolumes(true)
                .withForce(true)
                .exec()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getImage(name: String): Optional<Image<NativeImage>> {
        return Optional.ofNullable(
            client.listImagesCmd()
                .withShowAll(true)
                .exec()
                .firstOrNull { it.repoTags?.any { tag -> tag.startsWith(name) } == true }
                ?.let { nativeImage ->
                    ImageImpl(
                        registry = "",
                        repository = nativeImage.repoTags?.firstOrNull()?.substringBefore(':') ?: "",
                        tag = nativeImage.repoTags?.firstOrNull()?.substringAfter(':') ?: "latest",
                        nativeImage = nativeImage,
                        craftainer = this
                    )
                }
        )
    }

    override fun getImages(): List<Image<NativeImage>> {
        return client.listImagesCmd()
            .withShowAll(true)
            .exec()
            .map { nativeImage ->
                ImageImpl(
                    registry = "",
                    repository = nativeImage.repoTags?.firstOrNull()?.substringBefore(':') ?: "",
                    tag = nativeImage.repoTags?.firstOrNull()?.substringAfter(':') ?: "latest",
                    nativeImage = nativeImage,
                    craftainer = this
                )
            }
    }

    override suspend fun pullImage(
        registry: String,
        repository: String,
        tag: String
    ): Boolean = withContext(Dispatchers.IO) {

        try {
            client.pullImageCmd(repository)
                .withTag(tag)
                .withRegistry(registry.takeIf { it.isNotEmpty() })
                .exec(object : PullImageResultCallback() {
                    // eventually override fun onNext
                }).awaitCompletion()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun removeImage(name: String): Boolean {
        return try {
            client.removeImageCmd(name).exec()
            true
        } catch (e: Exception) {
            false
        }
    }


    override fun createVolume(
        name: String?,
    ): Volume<InspectVolumeResponse> {
        val createVolumeCmd = client.createVolumeCmd()
            .withName(name)
            .exec()

        val inspectVolumeResponse = client.inspectVolumeCmd(createVolumeCmd.name)
            .exec()

        return VolumeImpl(
            name = createVolumeCmd.name,
            inspectVolumeResponse = inspectVolumeResponse,
            craftainer = this
        )
    }

    override fun getVolume(name: String): Optional<Volume<InspectVolumeResponse>> {
        return Optional.ofNullable(
            client.inspectVolumeCmd(name).exec()?.let { inspectVolumeResponse ->
                VolumeImpl(
                    name = inspectVolumeResponse.name,
                    inspectVolumeResponse = inspectVolumeResponse,
                    craftainer = this
                )
            }
        )
    }

    override fun getVolumes(): List<Volume<InspectVolumeResponse>> {
        return client.listVolumesCmd().exec().volumes.map { volume ->
            VolumeImpl(
                name = volume.name,
                inspectVolumeResponse = volume,
                craftainer = this
            )
        }
    }

    override fun removeVolume(name: String) {
        client.removeVolumeCmd(name).exec()
    }

    override fun createNetwork(
        name: String,
        driver: Network.NetworkDriver,
        subnet: String?,
        gateway: String?
    ): Network<NativeNetwork> {
        val ipamConfig = if (subnet != null || gateway != null) {
            listOf(
                NativeNetwork.Ipam.Config()
                    .withSubnet(subnet)
                    .withGateway(gateway)
            )
        } else {
            emptyList()
        }

        val ipam = NativeNetwork.Ipam()
            .withConfig(ipamConfig)


        val networkResponse = client.createNetworkCmd()
            .withName(name)
            .withDriver(driver.toString().lowercase())
            .withIpam(ipam)
            .exec()

        val nativeNetwork = client.inspectNetworkCmd()
            .withNetworkId(networkResponse.id)
            .exec()

        return NetworkImpl(
            id = networkResponse.id,
            nativeNetwork = nativeNetwork,
            craftainer = this
        )
    }

    override fun getNetwork(id: String): Optional<Network<NativeNetwork>> {
        return Optional.ofNullable(
            client.listNetworksCmd()
                .withIdFilter(id)
                .exec()
                .firstOrNull()?.let { nativeNetwork ->
                    NetworkImpl(
                        id = nativeNetwork.id,
                        nativeNetwork = nativeNetwork,
                        craftainer = this
                    )
                }
        )
    }

    override fun getNetworkByName(name: String): Optional<Network<NativeNetwork>> {
        return Optional.ofNullable(
            client.listNetworksCmd()
                .withNameFilter(name)
                .exec()
                .firstOrNull()?.let { nativeNetwork ->
                    NetworkImpl(
                        id = nativeNetwork.id,
                        nativeNetwork = nativeNetwork,
                        craftainer = this
                    )
                }
        )
    }

    override fun getNetworks(): List<Network<NativeNetwork>> {
        return client.listNetworksCmd()
            .exec()
            .map { nativeNetwork ->
                NetworkImpl(
                    id = nativeNetwork.id,
                    nativeNetwork = nativeNetwork,
                    craftainer = this
                )
            }
    }

    override fun removeNetwork(id: String): Boolean {
        return try {
            client.removeNetworkCmd(id).exec()
            true
        } catch (e: Exception) {
            false
        }
    }
}