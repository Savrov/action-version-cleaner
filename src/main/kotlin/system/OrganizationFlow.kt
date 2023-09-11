package system

import domain.DeletePackagesUseCase
import domain.DeleteVersionsUseCase
import domain.LoadPackageVersionsUseCase
import domain.LoadPackagesByRepositoryUseCase
import model.Context
import model.Package
import model.Version

internal class OrganizationFlow(
    private val loadPackagesByRepositoryUseCase: LoadPackagesByRepositoryUseCase,
    private val loadPackageVersionsUseCase: LoadPackageVersionsUseCase,
    private val deletePackagesUseCase: DeletePackagesUseCase,
    private val deleteVersionsUseCase: DeleteVersionsUseCase,
) : AbstractFlow() {
    context (Context)
    override suspend fun loadPackages(): Result<Collection<Package>> {
        val params = LoadPackagesByRepositoryUseCase.Params(
            organization = organization,
            repository = repository,
            packageType = packageType
        )
        return loadPackagesByRepositoryUseCase(params)
    }

    context (Context)
    override suspend fun loadPackageVersions(packages: Collection<Package>): Result<Map<Package, Collection<Version>>> {
        return runCatching {
            packages.associate { p ->
                val params = LoadPackageVersionsUseCase.Params(
                    organization = p.owner.login,
                    packageName = p.name,
                    packageType = p.packageType
                )
                loadPackageVersionsUseCase(params).fold(
                    onSuccess = { p to it },
                    onFailure = { throw it }
                )
            }
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }

    context (Context)
    override suspend fun deletePackages(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<String>> {
        val dataToDelete = packageVersionMap.filterValues {
            it.count() == 1 && it.all { it.name.contains(snapshotTag) }
        }
        val params = DeletePackagesUseCase.Params(
            packages = dataToDelete.keys
        )
        return deletePackagesUseCase(params)
    }

    context (Context)
    override suspend fun deleteVersions(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<Int>> {
        val dataToDelete = packageVersionMap.filterValues {
            it.count() > 1 && it.all { it.name.contains(snapshotTag) }
        }
        val params = DeleteVersionsUseCase.Params(
            snapshotTag = snapshotTag,
            data = dataToDelete
        )
        return deleteVersionsUseCase(params)
    }
}