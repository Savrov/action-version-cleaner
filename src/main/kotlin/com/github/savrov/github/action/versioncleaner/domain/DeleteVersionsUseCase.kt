package com.github.savrov.github.action.versioncleaner.domain

import com.github.savrov.github.action.versioncleaner.model.ExceptionsBundle
import com.github.savrov.github.action.versioncleaner.model.OwnerType
import com.github.savrov.github.action.versioncleaner.model.PackageVersions

internal class DeleteVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<DeleteVersionsUseCase.Params, Result<Collection<Int>>> {

    override suspend fun invoke(input: Params): Result<Collection<Int>> {
        val versionIds = when (input.ownerType) {
            OwnerType.User ->
                input.data
                    .flatMap { item ->
                        versionRepository.deleteUserVersions(
                            user = item.owner,
                            packageName = item.packageName,
                            packageType = item.packageType,
                            versionIds = item.versionIds,
                        )
                    }

            OwnerType.Organisation ->
                input.data
                    .flatMap { item ->
                        versionRepository.deleteOrganisationVersions(
                            organisation = item.owner,
                            packageName = item.packageName,
                            packageType = item.packageType,
                            versionIds = item.versionIds,
                        )
                    }
        }
        val data = versionIds.flatMap {
            it.getOrNull()?.let { listOf(it) } ?: listOf()
        }
        val errors = versionIds.flatMap {
            it.exceptionOrNull()?.let { listOf(it) } ?: listOf()
        }
        return if (errors.isNotEmpty()) {
            Result.failure(ExceptionsBundle(errors))
        } else {
            Result.success(data)
        }
    }

    data class Params(
        val ownerType: OwnerType,
        val data: Collection<PackageVersions>,
    )
}
