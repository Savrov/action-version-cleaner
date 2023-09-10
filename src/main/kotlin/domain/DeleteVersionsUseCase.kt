package domain

import model.ExceptionsBundle
import model.Package
import model.Version

internal class DeleteVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<DeleteVersionsUseCase.Params, Result<Collection<Int>>> {

    override suspend fun invoke(input: Params): Result<Collection<Int>> {
        val result = input.data
            .flatMap { entry ->
                versionRepository.deleteVersions(
                    organization = entry.key.owner?.login ?: error("organization is missing"),
                    packageName = entry.key.name ?: error("packageName is missing"),
                    packageType = entry.key.packageType ?: error("packageType is missing"),
                    versionIds = entry.value
                        .filter { it.name?.contains(input.snapshotTag) ?: false }
                        .mapNotNull { it.id }
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
        val data: Map<Package, Collection<Version>>
    )
}