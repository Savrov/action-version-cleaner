package data

import model.Package
import model.PackageVersion

interface PackageDataSource {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deletePackage(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<Unit>

}