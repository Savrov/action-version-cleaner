package data

import model.Package
import model.PackageVersion

interface PackageDataSource {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun getPackageVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
        count: Int,
    ): Result<Collection<PackageVersion>>

}