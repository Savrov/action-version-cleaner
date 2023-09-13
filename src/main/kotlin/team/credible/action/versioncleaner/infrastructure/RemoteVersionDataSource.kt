package team.credible.action.versioncleaner.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import team.credible.action.versioncleaner.data.VersionDataSource
import team.credible.action.versioncleaner.model.NetworkException
import team.credible.action.versioncleaner.model.Version

internal class RemoteVersionDataSource(
    private val httpClient: HttpClient,
) : VersionDataSource {

    override suspend fun getOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<Version>> {
        return runCatching {
            httpClient.request("/orgs/$organisation/packages/$packageType/$packageName/versions") {
                method = HttpMethod.Get
                url {
                    parameters.append("page", "$page")
                    parameters.append("per_page", "100")
                    parameters.append("state", "active")
                }
            }.body<List<Version>>()
        }.onFailure {
            Result.failure<Collection<Version>>(NetworkException(it))
        }
    }

    override suspend fun deleteOrganisationVersion(
        versionId: Int,
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<Int> {
        return deleteVersion(
            url = "/orgs/$organisation/packages/$packageType/$packageName/versions/$versionId",
            versionId = versionId,
        )
    }

    override suspend fun getUserVersions(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>> {
        return runCatching {
            httpClient.request("/users/$user/packages/$packageType/$packageName/versions") {
                method = HttpMethod.Get
            }.body<List<Version>>()
        }.onFailure { Result.failure<Collection<Version>>(NetworkException(it)) }
    }

    override suspend fun deleteUserVersion(
        versionId: Int,
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Int> {
        return deleteVersion(
            url = "/users/$user/packages/$packageType/$packageName/versions/$versionId",
            versionId = versionId,
        )
    }

    private suspend fun deleteVersion(
        url: String,
        versionId: Int,
    ): Result<Int> {
        return runCatching {
            httpClient.request(url) {
                method = HttpMethod.Delete
            }
        }.fold(
            onSuccess = { Result.success(versionId) },
            onFailure = { Result.failure(NetworkException(it)) },
        )
    }
}
