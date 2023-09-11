package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.ExceptionsBundle
import team.credible.action.versioncleaner.model.Package

internal class DeleteOrganizationPackagesUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<DeleteOrganizationPackagesUseCase.Params, Result<Collection<String>>> {

    override suspend fun invoke(input: Params): Result<Collection<String>> {
        val result = packageRepository.deleteOrganizationPackages(input.packages)
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
        val packages: Collection<Package>,
    )
}
