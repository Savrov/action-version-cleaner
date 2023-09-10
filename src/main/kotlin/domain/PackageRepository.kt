package domain

import model.Package

interface PackageRepository {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deletePackages(
        data: Collection<Package>
    ): Collection<Result<String>>

}