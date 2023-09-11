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
import team.credible.action.versioncleaner.data.DefaultVersionRepository
import team.credible.action.versioncleaner.data.PackageDataSource
import team.credible.action.versioncleaner.data.VersionDataSource
import team.credible.action.versioncleaner.domain.DeletePackagesUseCase
import team.credible.action.versioncleaner.domain.DeleteVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadPackageVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadPackagesByRepositoryUseCase
import team.credible.action.versioncleaner.domain.PackageRepository
import team.credible.action.versioncleaner.domain.VersionRepository
import team.credible.action.versioncleaner.infrastructure.RemotePackageDataSource
import team.credible.action.versioncleaner.infrastructure.RemoteVersionDataSource
import team.credible.action.versioncleaner.model.Context
import team.credible.action.versioncleaner.system.OrganizationFlow

val module = module {

    single {
        Context(
            organization = "credible-team",
            repository = "gradle-versions",
            packageType = "maven",
            snapshotTag = "SNAPSHOT",
        )
        //    val (organization, repository) = koinApp.koin.getProperty<String>("GITHUB_REPOSITORY")?.split("/")
//        ?: error("environment variable GITHUB_REPOSITORY is missing")
//        Context(
//            organization = getPropertyOrNull<String>("GITHUB_REPOSITORY")?.split("/")?.getOrNull(0),
//            repository = getPropertyOrNull<String>("GITHUB_REPOSITORY")?.split("/")?.getOrNull(1),
//            packageType = getProperty("PACKAGE_TYPE"),
//            snapshotTag = getProperty("SNAPSHOT_TAG"),
//        )
    }

    single<PackageRepository> {
        DefaultPackageRepository(
            packageDataSource = get(),
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
        DeletePackagesUseCase(
            packageRepository = get(),
        )
    }

    factory {
        DeleteVersionsUseCase(
            versionRepository = get(),
        )
    }

    factory {
        LoadPackagesByRepositoryUseCase(
            packageRepository = get(),
        )
    }

    factory {
        LoadPackageVersionsUseCase(
            versionRepository = get(),
        )
    }

    single<PackageDataSource> {
        RemotePackageDataSource(
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
                headers.appendIfNameAbsent("Authorization", "Bearer ${getProperty<String>("TOKEN")}")
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
            loadPackagesByRepositoryUseCase = get(),
            loadPackageVersionsUseCase = get(),
            deletePackagesUseCase = get(),
            deleteVersionsUseCase = get(),
        )
    }
}
