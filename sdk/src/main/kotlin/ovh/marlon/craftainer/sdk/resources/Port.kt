package ovh.marlon.craftainer.sdk.resources

/**
 * Represents a port mapping between the host and the container.
 *
 * @property host The port on the host machine.
 * @property container The port on the container.
 */
data class Port(
    val host: Int,
    val container: Int,
    val protocol: String? = null
)