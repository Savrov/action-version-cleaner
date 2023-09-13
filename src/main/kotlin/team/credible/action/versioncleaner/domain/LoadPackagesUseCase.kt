package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Package

internal class LoadPackagesUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<LoadPackagesUseCase.Params, Result<Collection<Package>>> {

    override suspend fun invoke(input: Params): Result<Collection<Package>> {
        val packages = when (input.ownerType) {
            OwnerType.User -> packageRepository.loadUserPackages(
                user = input.owner,
                packageType = input.packageType,
            )

            OwnerType.Organisation -> packageRepository.loadOrganisationPackages(
                organisation = input.owner,
                packageType = input.packageType,
            )
        }
        return packages.map { list ->
            list.filter { item ->
                item.repository.name == input.repository
            }
        }
    }

    data class Params(
        val ownerType: OwnerType,
        val owner: String,
        val repository: String,
        val packageType: String,
    )
}
