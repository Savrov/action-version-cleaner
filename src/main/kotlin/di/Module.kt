package di

import data.DefaultPackageRepository
import data.DefaultVersionRepository
import data.PackageDataSource
import data.VersionDataSource
import domain.*
import domain.DeletePackagesUseCase
import domain.LoadPackageVersionsUseCase
import domain.LoadPackagesByRepositoryUseCase
import infrastructure.RemotePackageDataSource
import infrastructure.RemoteVersionDataSource
import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val module = module {

    single<PackageRepository> {
        DefaultPackageRepository(
            packageDataSource = get(),
            coroutineContext = Dispatchers.IO
        )
    }

    single<VersionRepository> {
        DefaultVersionRepository(
            versionDataSource = get(),
            coroutineContext = Dispatchers.IO
        )
    }

    factory {
        DeletePackagesUseCase(
            packageRepository = get()
        )
    }

    factory {
        LoadPackagesByRepositoryUseCase(
            packageRepository = get()
        )
    }

    factory {
        LoadPackageVersionsUseCase(
            versionRepository = get()
        )
    }

    single<PackageDataSource> {
        RemotePackageDataSource(
            httpClient = get()
        )
    }

    single<VersionDataSource> {
        RemoteVersionDataSource(
            httpClient = get()
        )
    }

    single<HttpClient> {
        HttpClient(Apache5) {
            expectSuccess = true
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("\n$message\n")
                    }
                }
                level = LogLevel.ALL
            }
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
                    }
                )
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
        }
    }

}