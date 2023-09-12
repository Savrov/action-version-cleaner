package team.credible.action.versioncleaner.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import team.credible.action.versioncleaner.data.DefaultPackageRepository
import team.credible.action.versioncleaner.data.DefaultRepositoryRepository
import team.credible.action.versioncleaner.data.DefaultVersionRepository
import team.credible.action.versioncleaner.data.PackageDataSource
import team.credible.action.versioncleaner.data.RepositoryDataSource
import team.credible.action.versioncleaner.data.VersionDataSource
import team.credible.action.versioncleaner.domain.DeleteOrganizationPackagesUseCase
import team.credible.action.versioncleaner.domain.DeleteOrganizationVersionsUseCase
import team.credible.action.versioncleaner.domain.IsRepositoryOwnedByUserUseCase
import team.credible.action.versioncleaner.domain.LoadOrganizationPackageVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadOrganizationPackagesByRepositoryUseCase
import team.credible.action.versioncleaner.domain.PackageRepository
import team.credible.action.versioncleaner.domain.RepositoryRepository
import team.credible.action.versioncleaner.domain.VersionRepository
import team.credible.action.versioncleaner.infrastructure.RemotePackageDataSource
import team.credible.action.versioncleaner.infrastructure.RemoteRepositoryDataSource
import team.credible.action.versioncleaner.infrastructure.RemoteVersionDataSource
import team.credible.action.versioncleaner.model.Context
import team.credible.action.versioncleaner.system.OrganizationFlow

val module = module {

    single {
        val (owner, repository) = getProperty<String>("GITHUB_REPOSITORY").split("/")
        Context(
            owner = owner,
            repository = repository,
            packageType = getProperty("PACKAGE_TYPE"),
            versionTag = getProperty("VERSION_TAG"),
        )
    }

    single<PackageRepository> {
        DefaultPackageRepository(
            packageDataSource = get(),
            coroutineContext = Dispatchers.IO,
        )
    }

    single<RepositoryRepository> {
        DefaultRepositoryRepository(
            repositoryDataSource = get(),
            coroutineContext = Dispatchers.IO,
        )
    }

    single<VersionRepository> {
        DefaultVersionRepository(
            versionDataSource = get(),
            coroutineContext = Dispatchers.IO,
        )
    }

    factory {
        DeleteOrganizationPackagesUseCase(
            packageRepository = get(),
        )
    }

    factory {
        IsRepositoryOwnedByUserUseCase(
            repositoryRepository = get(),
        )
    }

    factory {
        DeleteOrganizationVersionsUseCase(
            versionRepository = get(),
        )
    }

    factory {
        LoadOrganizationPackagesByRepositoryUseCase(
            packageRepository = get(),
        )
    }

    factory {
        LoadOrganizationPackageVersionsUseCase(
            versionRepository = get(),
        )
    }

    single<PackageDataSource> {
        RemotePackageDataSource(
            httpClient = get(),
        )
    }

    single<RepositoryDataSource> {
        RemoteRepositoryDataSource(
            httpClient = get(),
        )
    }

    single<VersionDataSource> {
        RemoteVersionDataSource(
            httpClient = get(),
        )
    }

    single<HttpClient> {
        HttpClient(Apache5) {
            expectSuccess = true
            install(DefaultRequest) {
                url("https://api.github.com")
                headers.appendIfNameAbsent("Accept", "application/vnd.github+json")
                headers.appendIfNameAbsent("Authorization", "Bearer ${getProperty<String>("GITHUB_TOKEN")}")
                headers.appendIfNameAbsent("X-GitHub-Api-Version", "2022-11-28")
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        isLenient = true
                        allowSpecialFloatingPointValues = true
                        allowStructuredMapKeys = true
                        prettyPrint = false
                        useArrayPolymorphism = false
                    },
                )
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
        }
    }

    single {
        OrganizationFlow(
            loadOrganizationPackagesByRepositoryUseCase = get(),
            loadOrganizationPackageVersionsUseCase = get(),
            deleteOrganizationPackagesUseCase = get(),
            deleteOrganizationVersionsUseCase = get(),
        )
    }
}
