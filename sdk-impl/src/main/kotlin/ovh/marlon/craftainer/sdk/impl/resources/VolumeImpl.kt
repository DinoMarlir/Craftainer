package ovh.marlon.craftainer.sdk.impl.resources

import com.github.dockerjava.api.command.InspectVolumeResponse
import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.resources.Volume

class VolumeImpl(
    override val name: String,
    private val inspectVolumeResponse: InspectVolumeResponse,
    private val craftainer: Craftainer<*, *, *, *>
): Volume<InspectVolumeResponse> {

    override val mountPoint: String
        get() = native().mountpoint

    override fun remove() {
        craftainer.removeVolume(name)
    }

    override fun native(): InspectVolumeResponse {
        return inspectVolumeResponse
    }
}