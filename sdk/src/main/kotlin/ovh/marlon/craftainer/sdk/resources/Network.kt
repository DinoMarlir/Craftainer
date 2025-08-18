package ovh.marlon.craftainer.sdk.resources

interface Network<N> {

    /**
     * The unique identifier of the network.
     */
    val id: String

    /**
     * The name of the network.
     */
    val name: String

    /**
     * The driver used for the network.
     */
    val driver: NetworkDriver

    /**
     * The subnet of the network.
     */
    val subnet: String

    /**
     * The gateway of the network.
     */
    val gateway: String

    /**
     * The creation time of the network.
     */
    val createdAt: Long

    /**
     * Removes the network.
     */
    fun remove()

    /**
     * Returns the native representation of the network.
     * This method is intended to provide access to the underlying network object.
     * @return The native network object, which can be of any type N.
     */
    fun native(): N

    enum class NetworkDriver {
        BRIDGE,
        HOST,
        OVERLAY,
        IPVLAN,
        MACVLAN,
        NONE
    }
}