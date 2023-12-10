package com.github.savrov.github.action.versioncleaner.di

import com.github.savrov.github.action.versioncleaner.data.DefaultRepositoryRepository
import com.github.savrov.github.action.versioncleaner.data.DefaultVersionRepository
import com.github.savrov.github.action.versioncleaner.data.PackageDataSource
import com.github.savrov.github.action.versioncleaner.data.RepositoryDataSource
import com.github.savrov.github.action.versioncleaner.data.VersionDataSource
import com.github.savrov.github.action.versioncleaner.domain.DeletePackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.DeleteVersionsUseCase
import com.github.savrov.github.action.versioncleaner.domain.FilterPackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.FilterVersionsUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadPackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadRepositoryOwnerTypeUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadVersionsUseCase
import com.github.savrov.github.action.versioncleaner.domain.PackageRepository
import com.github.savrov.github.action.versioncleaner.domain.RepositoryRepository
import com.github.savrov.github.action.versioncleaner.domain.VersionRepository
import com.github.savrov.github.action.versioncleaner.infrastructure.RemotePackageDataSource
import com.github.savrov.github.action.versioncleaner.infrastructure.RemoteRepositoryDataSource
import com.github.savrov.github.action.versioncleaner.infrastructure.RemoteVersionDataSource
import com.github.savrov.github.action.versioncleaner.model.Context
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

val module = module {

    single {
        val (owner, repository) = getProperty<String>("GITHUB_REPOSITORY").split("/")
        Context(
            owner = owner,
            repository = repository,
            packageType = getProperty("PACKAGE_TYPE"),
            versionTag = getProperty("VERSION_TAG"),
            isVersionTagStrict = getProperty<String>("IS_VERSION_TAG_STRICT") == "true",
        )
    }

    single<PackageRepository> {
        com.github.savrov.github.action.versioncleaner.data.DefaultPackageRepository(
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
        DeletePackagesUseCase(
            packageRepository = get(),
        )
    }

    factory {
        LoadRepositoryOwnerTypeUseCase(
            repositoryRepository = get(),
        )
    }

    factory {
        DeleteVersionsUseCase(
            versionRepository = get(),
        )
    }

    factory {
        FilterPackagesUseCase()
    }

    factory {
        FilterVersionsUseCase()
    }

    factory {
        LoadPackagesUseCase(
            packageRepository = get(),
        )
    }

    factory {
        LoadVersionsUseCase(
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
}
