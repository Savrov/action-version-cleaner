package team.credible.action.versioncleaner.infrastructure

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import team.credible.action.versioncleaner.data.PackageDataSource
import team.credible.action.versioncleaner.model.Package

internal class RemotePackageDataSource(
    private val httpClient: HttpClient
) : PackageDataSource {

    override suspend fun loadPackages(
        organization: String,
        packageType: String
    ): Result<Collection<Package>> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages") {
                method = HttpMethod.Get
                url {
                    parameters.append("package_type", packageType.lowercase())
                }
            }.body<List<Package>>()
        }
    }

    override suspend fun deletePackage(
        organization: String,
        packageName: String, packageType: String
    ): Result<String> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName") {
                method = HttpMethod.Delete
            }
        }.fold(
            onSuccess = { Result.success(packageName) },
            onFailure = { Result.failure(it) }
        )
    }

}