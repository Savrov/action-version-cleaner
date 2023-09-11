package team.credible.action.versioncleaner.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    @SerialName("login") val login: String,
    @SerialName("type") val type: String,
)
