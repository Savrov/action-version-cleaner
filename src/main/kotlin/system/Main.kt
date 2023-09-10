package system

import di.module
import domain.DeletePackagesUseCase
import domain.DeleteVersionsUseCase
import domain.LoadPackageVersionsUseCase
import domain.LoadPackagesByRepositoryUseCase
import kotlinx.coroutines.runBlocking
import model.Package
import model.Version
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

    val packages = loadOrganizationRepositoryPackages(
        loadPackagesByRepositoryUseCase = koinApp.koin.get<LoadPackagesByRepositoryUseCase>(),
        organization = organization,
        repository = repository,
        packageType = packageType,
    ).fold(
        onSuccess = {
            it
        },
        onFailure = {
            throw it
        }
    )

    val packageVersionMap = packages.associate { p ->
        loadOrganizationRepositoryPackageVersions(
            loadPackageVersionsUseCase = koinApp.koin.get<LoadPackageVersionsUseCase>(),
            organization = organization,
            packageName = p.name ?: error("package name is missing"),
            packageType = p.packageType ?: error("package type is missing")
        ).fold(
            onSuccess = { v ->
                p to v
            },
            onFailure = {
                throw it
            }
        )
    }

    deletePackages(
        koinApp.koin.get<DeletePackagesUseCase>(),
        packageVersionMap,
        snapshotTag,
    ).fold(
        onSuccess = {
            println("packages deleted: $it")
        },
        onFailure = {
            throw it
        }
    )

    deleteVersions(
        koinApp.koin.get<DeleteVersionsUseCase>(),
        packageVersionMap,
        snapshotTag,
    ).fold(
        onSuccess = {
            println("versions deleted: $it")
        },
        onFailure = {
            throw it
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
): Result<Collection<Version>> {
    val params = LoadPackageVersionsUseCase.Params(
        organization = organization,
        packageName = packageName,
        packageType = packageType,
    )
    return loadPackageVersionsUseCase(params)
}

private suspend fun deletePackages(
    deletePackagesUseCase: DeletePackagesUseCase,
    data: Map<Package, Collection<Version>>,
    snapshotTag: String,
): Result<Collection<String>> {
    val dataToDelete = data.filterValues {
        it.count() == 1 && it.all { it.name?.contains(snapshotTag) ?: false }
    }
    val params = DeletePackagesUseCase.Params(
        packages = dataToDelete.keys
    )
    return deletePackagesUseCase(params)
}

private suspend fun deleteVersions(
    deleteVersionsUseCase: DeleteVersionsUseCase,
    data: Map<Package, Collection<Version>>,
    snapshotTag: String,
): Result<Collection<Int>> {
    val dataToDelete = data.filterValues {
        it.count() > 1 && it.all { it.name?.contains(snapshotTag) ?: false }
    }
    val params = DeleteVersionsUseCase.Params(
        snapshotTag = snapshotTag,
        data = dataToDelete
    )
    return deleteVersionsUseCase(params)
}