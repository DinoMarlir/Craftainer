package ovh.marlon.craftainer.sdk.resources

interface Volume<V> {

    /**
     * The name of the volume.
     */
    val name: String

    /**
     * The mount point of the volume.
     */
    val mountPoint: String

    /**
     * Removes the volume.
     */
    fun remove()

    /**
     * Returns the native representation of the volume.
     * This method is intended to provide access to the underlying volume object.
     * @return The native volume object, which can be of any type V.
     */
    fun native(): V
}