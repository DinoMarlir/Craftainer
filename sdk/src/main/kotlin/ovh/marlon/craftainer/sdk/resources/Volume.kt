package ovh.marlon.craftainer.sdk.resources

interface Volume<V> {

    /**
     * The unique identifier of the volume.
     */
    val id: String

    /**
     * The name of the volume.
     */
    val name: String

    /**
     * The mount point of the volume.
     */
    val mountPoint: String

    /**
     * The creation time of the volume.
     */
    val createdAt: Long

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