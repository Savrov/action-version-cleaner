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

    override suspend fun loadPackages(
        organization: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages") {
                method = HttpMethod.Get
                url {
                    parameters.append("package_type", packageType.lowercase())
                }
            }.body<List<Package>>()
        }.onFailure {
            Result.failure<Collection<Package>>(NetworkException(it))
        }
    }

    override suspend fun deletePackage(
        organization: String,
        packageName: String,
        packageType: String,
    ): Result<String> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName") {
                method = HttpMethod.Delete
            }
        }.fold(
            onSuccess = { Result.success(packageName) },
            onFailure = { Result.failure(NetworkException(it)) },
        )
    }
}
