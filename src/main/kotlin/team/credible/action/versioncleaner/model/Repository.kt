package team.credible.action.versioncleaner.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    @SerialName("name") val name: String,
)
