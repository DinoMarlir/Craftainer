package ovh.marlon.craftainer.sdk.impl

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.InspectVolumeResponse
import com.github.dockerjava.api.command.PullImageResultCallback
import com.github.dockerjava.api.model.PullResponseItem
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.impl.resources.ImageImpl
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
        } catch (_: Exception) {
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