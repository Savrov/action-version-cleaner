package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Package(
    @SerialName("id") var id: Int? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("package_type") var packageType: String? = null,
    @SerialName("owner") var owner: Owner? = Owner(),
    @SerialName("version_count") var versionCount: Int? = null,
    @SerialName("visibility") var visibility: String? = null,
    @SerialName("url") var url: String? = null,
    @SerialName("created_at") var createdAt: String? = null,
    @SerialName("updated_at") var updatedAt: String? = null,
    @SerialName("repository") var repository: Repository? = Repository(),
    @SerialName("html_url") var htmlUrl: String? = null
)