package team.credible.action.versioncleaner.data

import team.credible.action.versioncleaner.model.Repository

interface RepositoryDataSource {

    suspend fun loadRepository(
        owner: String,
        repository: String,
    ): Result<Repository>

}