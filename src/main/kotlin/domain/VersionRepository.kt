package domain

import model.Version

interface VersionRepository {

    suspend fun loadVersions(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>>

    suspend fun deleteVersions(
        organization: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>>

}