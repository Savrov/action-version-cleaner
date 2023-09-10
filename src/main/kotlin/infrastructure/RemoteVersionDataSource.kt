package infrastructure

import data.VersionDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import model.Version

internal class RemoteVersionDataSource(
    private val httpClient: HttpClient
) : VersionDataSource {

    override suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<Version>> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName/versions") {
                method = HttpMethod.Get
                url {
                    parameters.append("page", "$page")
                    parameters.append("per_page", "100")
                    parameters.append("state", "active")
                }
            }.body<List<Version>>()
        }
    }

    override suspend fun deleteVersion(
        versionId: Int,
        organization: String,
        packageName: String,
        packageType: String
    ): Result<Int> {
        return runCatching {
            httpClient.request("/orgs/$organization/packages/$packageType/$packageName/versions/$versionId") {
                method = HttpMethod.Delete
            }
        }.fold(
            onSuccess = {
                Result.success(versionId)
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }
}