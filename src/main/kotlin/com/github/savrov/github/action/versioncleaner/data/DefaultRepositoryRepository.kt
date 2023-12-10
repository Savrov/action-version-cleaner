package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.domain.RepositoryRepository
import com.github.savrov.github.action.versioncleaner.model.Repository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class DefaultRepositoryRepository(
    private val repositoryDataSource: RepositoryDataSource,
    private val coroutineContext: CoroutineContext,
) : RepositoryRepository {
    override suspend fun loadRepository(owner: String, repository: String): Result<Repository> {
        return withContext(coroutineContext) {
            repositoryDataSource.loadRepository(owner, repository)
        }
    }
}
