package team.credible.action.versioncleaner.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Package(
    @SerialName("name") val name: String,
    @SerialName("package_type") val packageType: String,
    @SerialName("owner") val owner: Owner,
    @SerialName("repository") val repository: Repository,
)
