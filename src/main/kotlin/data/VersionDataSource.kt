package data

import model.Version

interface VersionDataSource {

    suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<Version>>

    suspend fun deleteVersion(
        versionId: Int,
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<Int>

}