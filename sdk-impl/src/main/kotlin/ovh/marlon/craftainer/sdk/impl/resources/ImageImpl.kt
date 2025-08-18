package ovh.marlon.craftainer.sdk.impl.resources

import ovh.marlon.craftainer.sdk.Craftainer
import ovh.marlon.craftainer.sdk.resources.Image
import com.github.dockerjava.api.model.Image as NativeImage

class ImageImpl(
    registry: String = "",
    repository: String,
    tag: String = "latest",
    private val nativeImage: NativeImage,
    private val craftainer: Craftainer<*, *, *, *>
): Image<NativeImage>(registry, repository, tag) {

    override suspend fun pull(): Boolean {
        return craftainer.pullImage(registry, repository, tag)
    }

    override fun isPulled(): Boolean {
        return craftainer.getImage("$registry/$repository:$tag").isPresent
    }

    override fun remove(): Boolean {
        return craftainer.removeImage(toString())
    }

    override fun native(): NativeImage {
        return nativeImage
    }
}