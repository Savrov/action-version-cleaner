package data

import model.PackageVersion

interface VersionDataSource {

    suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<PackageVersion>>

}