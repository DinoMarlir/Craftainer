package ovh.marlon.craftainer.sdk.impl

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.resources.*
import java.time.Duration
import java.util.*
import com.github.dockerjava.api.model.Container as NativeContainer
import com.github.dockerjava.api.model.Image as NativeImage
import com.github.dockerjava.api.model.Network as NativeNetwork
import com.github.dockerjava.api.model.Volume as NativeVolume


class CraftainerClient private constructor(config: DefaultDockerClientConfig): Craftainer<NativeContainer, NativeImage, NativeVolume, NativeNetwork>() {

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
        volumes: List<Volume<NativeVolume>>,
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
    ): Volume<NativeVolume> {
        TODO("Not yet implemented")
    }

    override fun getVolume(id: String): Optional<Volume<NativeVolume>> {
        TODO("Not yet implemented")
    }

    override fun getVolumeByName(name: String): Optional<Volume<NativeVolume>> {
        TODO("Not yet implemented")
    }

    override fun getVolumes(): List<Volume<NativeVolume>> {
        TODO("Not yet implemented")
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