package team.credible.action.versioncleaner.infrastructure

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import team.credible.action.versioncleaner.data.RepositoryDataSource
import team.credible.action.versioncleaner.model.NetworkException
import team.credible.action.versioncleaner.model.Repository

class RemoteRepositoryDataSource(
    private val httpClient: HttpClient,
) : RepositoryDataSource {
    override suspend fun loadRepository(owner: String, repository: String): Result<Repository> {
        return runCatching {
            httpClient.request("/repos/$owner/$repository") {
                method = HttpMethod.Get
            }.body<Repository>()
        }.onFailure {
            Result.failure<Repository>(NetworkException(it))
        }
    }
}
