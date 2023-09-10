package domain

import model.Package

internal class DeletePackagesUseCase(
    private val packageRepository: PackageRepository,
): SuspendUseCase<DeletePackagesUseCase.Params, Collection<Result<Unit>>> {

    override suspend fun invoke(input: Params): Collection<Result<Unit>> {
        return packageRepository.deletePackages(input.packages)
    }

    data class Params(
        val packages: Collection<Package>
    )
}