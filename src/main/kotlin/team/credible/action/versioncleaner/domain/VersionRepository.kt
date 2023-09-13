package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Version

interface VersionRepository {

    suspend fun loadOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>>

    suspend fun deleteOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>>

    suspend fun loadUserVersions(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>>

    suspend fun deleteUserVersions(
        user: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>>
}
