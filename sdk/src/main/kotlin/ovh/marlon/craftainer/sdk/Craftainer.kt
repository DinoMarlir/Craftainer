package ovh.marlon.craftainer.sdk

import ovh.marlon.craftainer.sdk.resources.Container
import ovh.marlon.craftainer.sdk.resources.Image
import ovh.marlon.craftainer.sdk.resources.Network
import ovh.marlon.craftainer.sdk.resources.Port
import ovh.marlon.craftainer.sdk.resources.Volume
import java.util.Optional

abstract class Craftainer<C, I, V, N> {

    abstract fun isDriverAvailable(): Boolean

    abstract fun createContainer(
        image: String,
        name: String? = null,
        ports: List<Port> = emptyList(),
        environment: Map<String, String> = emptyMap(),
        volumes: List<Volume<V>> = emptyList(),
        networks: List<Network<N>> = emptyList(),
        command: String? = null
    ): C

    abstract fun getContainer(id: String): Optional<Container<C, I>>

    abstract fun getContainerByName(name: String): Optional<Container<C, I>>

    abstract fun getContainers(): List<Container<C, I>>

    abstract fun getImage(name: String): Optional<Image<I>>


    abstract fun getImages(): List<Image<I>>

    abstract suspend fun pullImage(
        registry: String = "",
        repository: String,
        tag: String = "latest"
    ): Boolean

    abstract fun removeImage(name: String): Boolean

    abstract fun createVolume(name: String? = null): Volume<V>

    abstract fun getVolume(name: String): Optional<Volume<V>>

    abstract fun getVolumes(): List<Volume<V>>

    abstract fun removeVolume(name: String)

    abstract fun createNetwork(
        name: String,
        driver: Network.NetworkDriver = Network.NetworkDriver.BRIDGE,
        subnet: String? = null,
        gateway: String? = null
    ): Network<N>

    abstract fun getNetwork(id: String): Optional<Network<N>>

    abstract fun getNetworkByName(name: String): Optional<Network<N>>

    abstract fun getNetworks(): List<Network<N>>

    abstract fun removeNetwork(id: String): Boolean
}