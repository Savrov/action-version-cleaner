package domain

import model.Package

internal class LoadPackagesByRepositoryUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<LoadPackagesByRepositoryUseCase.Params, Result<Collection<Package>>> {

    override suspend fun invoke(input: Params): Result<Collection<Package>> {
        return packageRepository.loadPackages(
            organization = input.organization,
            packageType = input.packageType
        ).map { list ->
            list.filter { item ->
                item.repository.name == input.repository
            }
        }
    }

    data class Params(
        val organization: String,
        val repository: String,
        val packageType: String,
    )
}