package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.ExceptionsBundle
import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version

internal class DeleteVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<DeleteVersionsUseCase.Params, Collection<Int>> {

    override suspend fun invoke(input: Params): Result<Collection<Int>> {
        val versionIds = when (input.ownerType) {
            OwnerType.User ->
                input.data
                    .flatMap { entry ->
                        versionRepository.deleteUserVersions(
                            user = entry.key.owner.login,
                            packageName = entry.key.name,
                            packageType = entry.key.packageType,
                            versionIds = entry.value
                                .filter { it.name.contains(input.versionTag) }
                                .map { it.id },
                        )
                    }

            OwnerType.Organisation ->
                input.data
                    .flatMap { entry ->
                        versionRepository.deleteOrganizationVersions(
                            organization = entry.key.owner.login,
                            packageName = entry.key.name,
                            packageType = entry.key.packageType,
                            versionIds = entry.value
                                .filter { it.name.contains(input.versionTag) }
                                .map { it.id },
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
        val versionTag: String,
        val data: Map<Package, Collection<Version>>,
    )
}
