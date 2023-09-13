package team.credible.action.versioncleaner.model

data class PackageVersions(
    val owner: String,
    val packageName: String,
    val packageType: String,
    val versionIds: Collection<Int>,
)
