package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.ExceptionsBundle
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version

internal class DeleteOrganizationVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<DeleteOrganizationVersionsUseCase.Params, Result<Collection<Int>>> {

    override suspend fun invoke(input: Params): Result<Collection<Int>> {
        val result = input.data
            .flatMap { entry ->
                versionRepository.deleteOrganizationVersions(
                    organization = entry.key.owner.login,
                    packageName = entry.key.name,
                    packageType = entry.key.packageType,
                    versionIds = entry.value
                        .filter { it.name.contains(input.snapshotTag) }
                        .map { it.id },
                )
            }
        val versionIds = result.flatMap {
            it.getOrNull()?.let { listOf(it) } ?: listOf()
        }
        val errors = result.flatMap {
            it.exceptionOrNull()?.let { listOf(it) } ?: listOf()
        }
        return if (errors.isNotEmpty()) {
            Result.failure(ExceptionsBundle(errors))
        } else {
            Result.success(versionIds)
        }
    }

    data class Params(
        val snapshotTag: String,
        val data: Map<Package, Collection<Version>>,
    )
}
