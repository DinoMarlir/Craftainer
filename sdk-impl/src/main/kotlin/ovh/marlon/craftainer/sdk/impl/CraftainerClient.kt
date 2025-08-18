package ovh.marlon.craftainer.sdk.impl

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.InspectVolumeResponse
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.impl.resources.VolumeImpl
import ovh.marlon.craftainer.sdk.resources.*
import java.time.Duration
import java.util.*
import com.github.dockerjava.api.model.Container as NativeContainer
import com.github.dockerjava.api.model.Image as NativeImage
import com.github.dockerjava.api.model.Network as NativeNetwork


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
        } catch (e: Exception) {
            false
        }
    }

    override fun createContainer(
        image: String,
        name: String?,
        ports: List<Port>,
        environment: Map<String, String>,
        volumes: List<Volume<InspectVolumeResponse>>,
        networks: List<Network<NativeNetwork>>,
        command: String?
    ): NativeContainer {
        TODO("Not yet implemented")
    }

    override fun getContainer(id: String): Optional<Container<NativeContainer, NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun getContainerByName(name: String): Optional<Container<NativeContainer, NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun getContainers(): List<Container<NativeContainer, NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun getImage(id: String): Optional<Image<NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun getImageByName(name: String): Optional<Image<NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun getImages(): List<Image<NativeImage>> {
        TODO("Not yet implemented")
    }

    override fun createVolume(
        name: String?,
        mountPoint: String?
    ): Volume<InspectVolumeResponse> {
        TODO("Not yet implemented")
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
        name: String?,
        driver: Network.NetworkDriver,
        subnet: String?,
        gateway: String?
    ): Network<NativeNetwork> {
        TODO("Not yet implemented")
    }

    override fun getNetwork(id: String): Optional<Network<NativeNetwork>> {
        TODO("Not yet implemented")
    }

    override fun getNetworkByName(name: String): Optional<Network<NativeNetwork>> {
        TODO("Not yet implemented")
    }

    override fun getNetworks(): List<Network<NativeNetwork>> {
        TODO("Not yet implemented")
    }

}