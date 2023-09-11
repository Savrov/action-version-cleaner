package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Repository

interface RepositoryRepository {

    suspend fun loadRepository(
        owner: String,
        repository: String,
    ): Result<Repository>
}
