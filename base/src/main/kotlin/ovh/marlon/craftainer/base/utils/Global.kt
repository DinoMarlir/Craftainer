package ovh.marlon.craftainer.base.utils

import kotlinx.serialization.json.Json

object Constants {
    const val CRAFTAINER_NETWORK_NAME = "craftainer_network"
    const val CRAFTAINER_RESOURCE_LABEL = "craftainer-resource"
    const val CRAFTAINER_VOLUME_MOUNT_PATH = "./data"
    val JSON = Json { prettyPrint = true; isLenient = true; ignoreUnknownKeys = true }
}