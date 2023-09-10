package infrastructure

import data.PackageDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import model.Package
import model.PackageVersion

internal class RemotePackageDataSource(
    private val httpClient: HttpClient
) : PackageDataSource {

    override suspend fun loadPackages(organization: String, packageType: String): Result<Collection<Package>> {
        return runCatching {
            val response = httpClient.request("/orgs/$organization/packages") {
                method = HttpMethod.Get
                url {
                    parameters.append("package_type", packageType.lowercase())
                }
            }
            println("""
                loadPackages response:
                $response
            """.trimIndent())
            response.body<List<Package>>()
        }.fold(
            onSuccess = {
                Result.success(it)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override suspend fun getPackageVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
        count: Int
    ): Result<Collection<PackageVersion>> {
        TODO("Not yet implemented")
    }
}