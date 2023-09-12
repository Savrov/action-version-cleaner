package team.credible.action.versioncleaner.data

import team.credible.action.versioncleaner.model.Package

interface PackageDataSource {

    suspend fun loadOrganizationPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteOrganizationPackage(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<String>

    suspend fun loadUserPackages(
        user: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteUserPackage(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<String>
}
