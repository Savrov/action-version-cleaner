package system

import di.module
import domain.DeletePackagesUseCase
import domain.LoadPackageVersionsUseCase
import domain.LoadPackagesByRepositoryUseCase
import kotlinx.coroutines.runBlocking
import model.Package
import model.PackageVersion
import org.koin.core.context.startKoin
import org.koin.environmentProperties

fun main() = runBlocking {
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }

//    val (organization, repository) = koinApp.koin.getProperty<String>("GITHUB_REPOSITORY")?.split("/")
//        ?: error("environment variable GITHUB_REPOSITORY is missing")
//    val packageType = koinApp.koin.getProperty<String>("PACKAGE_TYPE")
//        ?: error("environment variable PACKAGE_TYPE is missing")
    val organization = "credible-team"
    val repository = "gradle-versions"
    val packageType = "maven"
    val snapshotTag = "SNAPSHOT"
    println(
        """
        | organization=$organization,
        | repository=$repository,
        | packageType=$packageType
        """.trimIndent()
    )
    loadOrganizationRepositoryPackages(
        loadPackagesByRepositoryUseCase = koinApp.koin.get<LoadPackagesByRepositoryUseCase>(),
        organization = organization,
        repository = repository,
        packageType = packageType,
    ).fold(
        onSuccess = {
            val packageWithVersions = it.associateWith {
                loadOrganizationRepositoryPackageVersions(
                    loadPackageVersionsUseCase = koinApp.koin.get<LoadPackageVersionsUseCase>(),
                    organization = organization,
                    packageName = it.name ?: error("package name is missing"),
                    packageType = it.packageType ?: error("package type is missing")
                ).fold(
                    onSuccess = {
                        it
                    },
                    onFailure = {
                        TODO("Handle failure. Stop execution")
                    }
                )
            }
            println("packageWithVersions count = ${packageWithVersions.count()}")
            deletePackageSnapshotVersion(
                koinApp.koin.get<DeletePackagesUseCase>(),
                packageWithVersions,
                snapshotTag,
            )
        },
        onFailure = {
            TODO("Handle failure. Stop execution")
        }
    )

}

private suspend fun loadOrganizationRepositoryPackages(
    loadPackagesByRepositoryUseCase: LoadPackagesByRepositoryUseCase,
    organization: String,
    repository: String,
    packageType: String,
): Result<Collection<Package>> {
    val params = LoadPackagesByRepositoryUseCase.Params(
        organization = organization,
        repository = repository,
        packageType = packageType
    )
    return loadPackagesByRepositoryUseCase(params)
}

private suspend fun loadOrganizationRepositoryPackageVersions(
    loadPackageVersionsUseCase: LoadPackageVersionsUseCase,
    organization: String,
    packageName: String,
    packageType: String,
): Result<Collection<PackageVersion>> {
    val params  = LoadPackageVersionsUseCase.Params(
        organization = organization,
        packageName = packageName,
        packageType = packageType,
    )
    return loadPackageVersionsUseCase(params)
}

private suspend fun deletePackageSnapshotVersion(
    deletePackagesUseCase: DeletePackagesUseCase,
    packageWithVersion: Map<Package, Collection<PackageVersion>>,
    snapshotTag: String,
) {
    val packageVersionsToDelete = packageWithVersion.filterValues {
        it.count() > 1 && it.any { it.name == snapshotTag }
    }.takeIf { it.isNotEmpty() }?.let {
        TODO("delete SNAPSHOT version")
    }
    // packages to delete
    packageWithVersion.filterValues {
        (it.count() == 1 && it.any { it.name == snapshotTag }) || it.isEmpty()
    }.takeIf { it.isNotEmpty() }?.let {
        val params = DeletePackagesUseCase.Params(
            packages = it.keys
        )
        deletePackagesUseCase(params).forEach {
            it.fold(
                onSuccess = {
                    println("package deleted")
                },
                onFailure = {
                    println("package can not be delete. Ex: $it")
                }
            )
        }
    }

}
