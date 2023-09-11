package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Version

interface VersionRepository {

    suspend fun loadOrganizationVersions(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>>

    suspend fun deleteOrganizationVersions(
        organization: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>>
}
