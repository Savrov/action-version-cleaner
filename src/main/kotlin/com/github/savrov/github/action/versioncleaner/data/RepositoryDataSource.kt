package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.model.Repository

interface RepositoryDataSource {

    suspend fun loadRepository(
        owner: String,
        repository: String,
    ): Result<Repository>
}
