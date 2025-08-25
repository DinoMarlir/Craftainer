package ovh.marlon.craftainer.sdk.resources

/**
 * Represents a container in the Craftainer SDK.
 *
 * This interface defines the properties and methods that a container resource should implement.
 * It includes details such as the container's ID, name, image, status, and creation time.
 */
interface Container<C, I> {

    /**
     * The unique identifier of the container.
     */
    val id: String

    /**
     * The name of the container.
     */
    val name: String?

    /**
     * The image used to create the container.
     */
    val imageName: String?

    /**
     * The status of the container.
     */
    val status: ContainerStatus

    /**
     * The exposed ports of the container.
     */
    val exposedPorts: List<Port>

    /**
     * The creation time of the container.
     */
    val createdAt: Long

    /**
     * The environment variables set in the container.
     */
    val environment: Map<String, String>

    /**
     * The labels assigned to the container.
     */
    val labels: Map<String, String>

    /**
     * The configuration of the container.
     */
    fun run()

    /**
     * Stops the container.
     */
    fun stop()

    /**
     * Restarts the container.
     */
    fun restart()

    /**
     * Returns the native representation of the container.
     * This method is intended to provide access to the underlying container object
     * @return The native container object, which can be of any type C.
     */
    fun native(): C

    /**
     * Removes the container.
     */

    /**
     * Represents the status of a container in the Craftainer SDK.
     *
     * This enum defines various states a container can be in, such as running, stopped, paused, etc.
     * It also provides a method to convert a string representation of the status into the corresponding enum value.
     */
    enum class ContainerStatus {
        HEALTHY,
        STARTING,
        RUNNING,
        STOPPED,
        PAUSED,
        RESTARTING,
        EXITED,
        DEAD,
        UNKNOWN;

        companion object {

            /**
             * Converts a string representation of a container status to the corresponding enum value.
             *
             * @param status The string representation of the container status.
             * @return The corresponding [ContainerStatus] enum value, or null if no match is found.
             */
            fun fromString(status: String): ContainerStatus? {
                return entries.find { it.name.equals(status, ignoreCase = true) }
            }
        }
    }
}