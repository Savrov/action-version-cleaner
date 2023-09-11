package team.credible.action.versioncleaner.data

import team.credible.action.versioncleaner.model.Package

interface PackageDataSource {

    suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deletePackage(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<String>

}