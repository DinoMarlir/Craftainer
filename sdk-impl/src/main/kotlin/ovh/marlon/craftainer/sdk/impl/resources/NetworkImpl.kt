package ovh.marlon.craftainer.sdk.impl.resources

import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.resources.Network
import com.github.dockerjava.api.model.Network as NativeNetwork

class NetworkImpl(
    override val id: String,
    private val nativeNetwork: NativeNetwork,
    private val craftainer: Craftainer<*, *, *, *>
) : Network<NativeNetwork> {

    override val name: String?
        get() = nativeNetwork.name
    override val driver: Network.NetworkDriver
        get() = Network.NetworkDriver.fromString(nativeNetwork.driver.uppercase())
    override val subnet: String?
        get() = nativeNetwork.ipam?.config?.firstOrNull()?.subnet
    override val gateway: String?
        get() = nativeNetwork.ipam?.config?.firstOrNull()?.gateway
    override val createdAt: Long
        get() = nativeNetwork.created?.time ?: 0L

    override fun remove() {
        craftainer.removeNetwork(id)
    }

    override fun native(): NativeNetwork {
        return nativeNetwork
    }
}