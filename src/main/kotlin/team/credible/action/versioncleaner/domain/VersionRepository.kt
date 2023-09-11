package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Version

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