package ovh.marlon.craftainer.sdk.resources

abstract class Image<I>(
    val registry: String = "",
    val repository: String,
    val tag: String = "latest",
) {

    /**
     * Pulls the image from the registry.
     *
     * This method is responsible for downloading the image from the specified registry.
     * It should handle any necessary authentication and network operations.
     *
     * @return true if the image was successfully pulled, false otherwise.
     */
    abstract suspend fun pull(): Boolean

    /**
     * Checks if the image has been pulled.
     *
     * This method should determine whether the image is already available locally.
     * It can be used to avoid unnecessary network operations when the image is already present.
     *
     * @return true if the image has been pulled, false otherwise.
     */
    abstract fun isPulled(): Boolean

    /**
     * Removes the image from the local storage.
     *
     * This method should handle the removal of the image from the local system.
     * It may involve deleting files or cleaning up resources associated with the image.
     *
     * @return true if the image was successfully removed, false otherwise.
     */
    abstract fun remove(): Boolean

    /**
     * Returns the native representation of the image.
     *
     * This method is intended to provide access to the underlying image object,
     * which can be of any type I. It allows for interaction with the image in a way
     * that is specific to the implementation.
     *
     * @return The native image object, which can be of any type I.
     */
    abstract fun native(): I

    /**
     * Returns the full image name in the format "registry/repository:tag".
     *
     * @return The full image name as a string.
     */
    override fun toString(): String {
        return if (registry.isNotEmpty()) {
            "$registry/$repository:$tag"
        } else {
            "$repository:$tag"
        }
    }
}