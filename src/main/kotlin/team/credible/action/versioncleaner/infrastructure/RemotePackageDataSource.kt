package team.credible.action.versioncleaner.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import team.credible.action.versioncleaner.data.PackageDataSource
import team.credible.action.versioncleaner.model.NetworkException
import team.credible.action.versioncleaner.model.Package

internal class RemotePackageDataSource(
    private val httpClient: HttpClient,
) : PackageDataSource {

    override suspend fun loadOrganisationPackages(
        organisation: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return loadPackages("/orgs/$organisation/packages", packageType)
    }

    override suspend fun deleteOrganisationPackage(
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<String> {
        return deletePackage("/orgs/$organisation/packages/$packageType/$packageName", packageName)
    }

    override suspend fun loadUserPackages(
        user: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return loadPackages("/users/$user/packages", packageType)
    }

    override suspend fun deleteUserPackage(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<String> {
        return deletePackage("/users/$user/packages/$packageType/$packageName", packageName)
    }

    private suspend fun loadPackages(url: String, packageType: String): Result<Collection<Package>> {
        return runCatching {
            httpClient.request(url) {
                method = HttpMethod.Get
                url {
                    parameters.append("package_type", packageType.lowercase())
                }
            }.body<List<Package>>()
        }.onFailure {
            Result.failure<Collection<Package>>(NetworkException(it))
        }
    }

    private suspend fun deletePackage(url: String, packageName: String): Result<String> {
        return runCatching {
            httpClient.request(url) {
                method = HttpMethod.Delete
            }
        }.fold(
            onSuccess = { Result.success(packageName) },
            onFailure = { Result.failure(NetworkException(it)) },
        )
    }
}
