package domain

import model.PackageVersion

interface VersionRepository {

    suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<PackageVersion>>

}