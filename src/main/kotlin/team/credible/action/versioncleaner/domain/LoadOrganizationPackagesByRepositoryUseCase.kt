package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Package

internal class LoadOrganizationPackagesByRepositoryUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<LoadOrganizationPackagesByRepositoryUseCase.Params, Result<Collection<Package>>> {

    override suspend fun invoke(input: Params): Result<Collection<Package>> {
        return packageRepository.loadOrganizationPackages(
            organization = input.organization,
            packageType = input.packageType,
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
