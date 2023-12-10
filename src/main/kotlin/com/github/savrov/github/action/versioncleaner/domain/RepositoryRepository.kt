package com.github.savrov.github.action.versioncleaner.domain

import com.github.savrov.github.action.versioncleaner.model.Repository

interface RepositoryRepository {

    suspend fun loadRepository(
        owner: String,
        repository: String,
    ): Result<Repository>
}
