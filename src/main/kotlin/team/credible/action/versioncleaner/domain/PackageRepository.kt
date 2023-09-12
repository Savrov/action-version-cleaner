package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Package

interface PackageRepository {

    suspend fun loadOrganizationPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteOrganizationPackages(
        data: Collection<Package>,
    ): Collection<Result<String>>

    suspend fun loadUserPackages(
        user: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteUserPackages(
        data: Collection<Package>,
    ): Collection<Result<String>>
}
