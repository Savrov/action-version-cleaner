package com.github.savrov.github.action.versioncleaner.infrastructure

import com.github.savrov.github.action.versioncleaner.data.RepositoryDataSource
import com.github.savrov.github.action.versioncleaner.model.NetworkException
import com.github.savrov.github.action.versioncleaner.model.Repository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod

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
