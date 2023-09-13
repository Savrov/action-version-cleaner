package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Package

interface PackageRepository {

    suspend fun loadOrganisationPackages(
        organisation: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteOrganisationPackages(
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
