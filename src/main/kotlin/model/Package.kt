package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Package(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("package_type") val packageType: String? = null,
    @SerialName("owner") val owner: Owner? = null,
    @SerialName("version_count") val versionCount: Int? = null,
    @SerialName("visibility") val visibility: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("repository") val repository: Repository? = null,
    @SerialName("html_url") val htmlUrl: String? = null
)