package ovh.marlon.craftainer.sdk.impl.resources

import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.resources.Container
import ovh.marlon.craftainer.sdk.resources.Port
import com.github.dockerjava.api.model.Image as NativeImage
import com.github.dockerjava.api.model.Container as NativeContainer

class ContainerImpl(
    override val id: String,
    private val nativeContainer: NativeContainer,
    private val craftainer: Craftainer<*, *, *, *>
) : Container<NativeContainer, NativeImage> {

    override val name: String?
        get() = nativeContainer.names.firstOrNull()?.removePrefix("/") // Docker names start with a slash
    override val imageName: String?
        get() = nativeContainer.image
    override val status: Container.ContainerStatus
        get() = Container.ContainerStatus.fromString(nativeContainer.status) ?: Container.ContainerStatus.UNKNOWN
    override val exposedPorts: List<Port>
        get() = nativeContainer.ports.mapNotNull { port ->
            port.privatePort?.let {
                Port(
                    it,
                    port.publicPort ?: 0,
                    port.type
                )
            }
        }
    override val createdAt: Long
        get() = nativeContainer.created ?: 0L
    override val environment: Map<String, String>
        get() = TODO("Not yet implemented")

    override fun run() {
        craftainer.runContainer(id)
    }

    override fun stop() {
        craftainer.stopContainer(id)
    }

    override fun restart() {
        craftainer.restartContainer(id)
    }

    override fun native(): NativeContainer {
        return nativeContainer
    }
}