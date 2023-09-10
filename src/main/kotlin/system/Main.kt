package system

import di.module
import domain.LoadPackagesByRepositoryUseCase
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.environmentProperties

fun main() = runBlocking {
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }

    val (organization, repository) = koinApp.koin.getProperty<String>("GITHUB_REPOSITORY")?.split("/")
        ?: error("environment variable GITHUB_REPOSITORY is missing")
    val packageType = koinApp.koin.getProperty<String>("PACKAGE_TYPE")
        ?: error("environment variable PACKAGE_TYPE is missing")
    parseOrganizationRepositoryPackages(
        loadPackagesByRepositoryUseCase = koinApp.koin.get<LoadPackagesByRepositoryUseCase>(),
        organization = organization,
        repository = repository,
        packageType = packageType,
    )

}

private suspend fun parseOrganizationRepositoryPackages(
    loadPackagesByRepositoryUseCase: LoadPackagesByRepositoryUseCase,
    organization: String,
    repository: String,
    packageType: String,
) {
    val loadPackagesByRepositoryParams = LoadPackagesByRepositoryUseCase.Params(
        organization = organization,
        repository = repository,
        packageType = packageType
    )
    val loadPackagesByRepositoryResult = loadPackagesByRepositoryUseCase(loadPackagesByRepositoryParams)
    loadPackagesByRepositoryResult.fold(
        onSuccess = {
            println("packages=$it")
        },
        onFailure = {
            println("ex=$it")
        }
    )
}