package di

import data.DefaultPackageRepository
import data.PackageDataSource
import domain.LoadPackagesByRepositoryUseCase
import domain.PackageRepository
import infrastructure.RemotePackageDataSource
import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val module = module {

    single<PackageRepository> {
        DefaultPackageRepository(
            packageDataSource = get(),
            coroutineContext = Dispatchers.IO
        )
    }

    factory {
        LoadPackagesByRepositoryUseCase(
            packageRepository = get()
        )
    }

    single<PackageDataSource> {
        RemotePackageDataSource(
            httpClient = get()
        )
    }

    single<HttpClient> {
        HttpClient(Apache5) {
            install(Logging)
            install(DefaultRequest) {
                url("https://api.github.com")
                headers.appendIfNameAbsent("Accept", "application/vnd.github+json")
                headers.appendIfNameAbsent("Authorization", "Bearer ${getProperty<String>("TOKEN")}")
                headers.appendIfNameAbsent("X-GitHub-Api-Version", "2022-11-28")
            }
            install(ContentNegotiation) {
                json()
            }
        }
    }

}