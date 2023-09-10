package infrastructure

import data.PackageDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import model.Package

internal class RemotePackageDataSource(
    private val httpClient: HttpClient
) : PackageDataSource {

    override suspend fun loadPackages(organization: String, packageType: String): Result<Collection<Package>> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages") {
                method = HttpMethod.Get
                url {
                    parameters.append("package_type", packageType.lowercase())
                }
            }.body<List<Package>>()
        }
    }

    override suspend fun deletePackage(organization: String, packageName: String, packageType: String): Result<Unit> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName") {
                method = HttpMethod.Delete
            }
        }
    }

}