package infrastructure

import data.VersionDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import model.PackageVersion

internal class RemoteVersionDataSource(
    private val httpClient: HttpClient
) : VersionDataSource {

    override suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<PackageVersion>> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName/versions") {
                method = HttpMethod.Get
                url {
                    parameters.append("page", "$page")
                    parameters.append("per_page", "100")
                    parameters.append("state", "active")
                }
            }.body<List<PackageVersion>>()
        }
    }
}