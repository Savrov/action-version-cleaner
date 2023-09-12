package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.ExceptionsBundle
import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Package

internal class DeletePackagesUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<DeletePackagesUseCase.Params, Collection<String>> {

    override suspend fun invoke(input: Params): Result<Collection<String>> {
        val result = when (input.ownerType) {
            OwnerType.User -> packageRepository.deleteUserPackages(input.packages)
            OwnerType.Organisation -> packageRepository.deleteOrganizationPackages(input.packages)
        }
        val names = result.flatMap {
            it.getOrNull()?.let { listOf(it) } ?: listOf()
        }
        val errors = result.flatMap {
            it.exceptionOrNull()?.let { listOf(it) } ?: listOf()
        }
        return if (errors.isNotEmpty()) {
            Result.failure(ExceptionsBundle(errors))
        } else {
            Result.success(names)
        }
    }

    data class Params(
        val ownerType: OwnerType,
        val packages: Collection<Package>,
    )
}
