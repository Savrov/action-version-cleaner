package domain

import model.Package

interface PackageRepository {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

}