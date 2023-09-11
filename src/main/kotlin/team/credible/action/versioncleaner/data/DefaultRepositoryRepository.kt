package team.credible.action.versioncleaner.data

import kotlinx.coroutines.withContext
import team.credible.action.versioncleaner.domain.RepositoryRepository
import team.credible.action.versioncleaner.model.Repository
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
