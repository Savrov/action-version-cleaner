package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageVersion(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("package_html_url") val packageHtmlUrl: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("html_url") val htmlUrl: String? = null,
)