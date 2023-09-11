package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Package

interface PackageRepository {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deletePackages(
        data: Collection<Package>
    ): Collection<Result<String>>

}