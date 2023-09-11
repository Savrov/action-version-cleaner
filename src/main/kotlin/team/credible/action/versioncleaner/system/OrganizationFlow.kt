package team.credible.action.versioncleaner.system

import team.credible.action.versioncleaner.domain.DeleteOrganizationPackagesUseCase
import team.credible.action.versioncleaner.domain.DeleteOrganizationVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadOrganizationPackageVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadOrganizationPackagesByRepositoryUseCase
import team.credible.action.versioncleaner.model.Context
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version

internal class OrganizationFlow(
    private val loadOrganizationPackagesByRepositoryUseCase: LoadOrganizationPackagesByRepositoryUseCase,
    private val loadOrganizationPackageVersionsUseCase: LoadOrganizationPackageVersionsUseCase,
    private val deleteOrganizationPackagesUseCase: DeleteOrganizationPackagesUseCase,
    private val deleteOrganizationVersionsUseCase: DeleteOrganizationVersionsUseCase,
) : AbstractFlow() {
    context (Context)
    override suspend fun loadPackages(): Result<Collection<Package>> {
        val params = LoadOrganizationPackagesByRepositoryUseCase.Params(
            organization = owner,
            repository = repository,
            packageType = packageType,
        )
        return loadOrganizationPackagesByRepositoryUseCase(params)
    }

    context (Context)
    override suspend fun loadPackageVersions(packages: Collection<Package>): Result<Map<Package, Collection<Version>>> {
        return runCatching {
            packages.associate { p ->
                val params = LoadOrganizationPackageVersionsUseCase.Params(
                    organization = p.owner.login,
                    packageName = p.name,
                    packageType = p.packageType,
                )
                loadOrganizationPackageVersionsUseCase(params).fold(
                    onSuccess = { p to it },
                    onFailure = { throw it },
                )
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) },
        )
    }

    context (Context)
    override suspend fun deletePackages(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<String>> {
        val dataToDelete = packageVersionMap.filterValues {
            it.count() == 1 && it.all { it.name.contains(versionTag) }
        }
        val params = DeleteOrganizationPackagesUseCase.Params(
            packages = dataToDelete.keys,
        )
        return deleteOrganizationPackagesUseCase(params)
    }

    context (Context)
    override suspend fun deleteVersions(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<Int>> {
        val dataToDelete = packageVersionMap.filterValues {
            it.count() > 1 && it.all { it.name.contains(versionTag) }
        }
        val params = DeleteOrganizationVersionsUseCase.Params(
            versionTag = versionTag,
            data = dataToDelete,
        )
        return deleteOrganizationVersionsUseCase(params)
    }
}
