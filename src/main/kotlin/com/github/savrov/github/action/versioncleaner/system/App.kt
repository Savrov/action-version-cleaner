package com.github.savrov.github.action.versioncleaner.system

import com.github.savrov.github.action.versioncleaner.di.module
import com.github.savrov.github.action.versioncleaner.domain.DeletePackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.DeleteVersionsUseCase
import com.github.savrov.github.action.versioncleaner.domain.FilterPackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.FilterVersionsUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadPackagesUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadRepositoryOwnerTypeUseCase
import com.github.savrov.github.action.versioncleaner.domain.LoadVersionsUseCase
import com.github.savrov.github.action.versioncleaner.model.Context
import com.github.savrov.github.action.versioncleaner.model.OwnerType
import com.github.savrov.github.action.versioncleaner.model.Package
import com.github.savrov.github.action.versioncleaner.model.PackageVersions
import com.github.savrov.github.action.versioncleaner.model.Version
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

suspend fun main() {
    val logger = LoggerFactory.getLogger("main")
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }
    with(logger) {
        with(koinApp.koin) {
            val context = get<Context>()
            runCatching {
                val ownerType = loadOwnerType(context, get())
                logger.info("repository owner: ${ownerType.name.lowercase()}")
                val packages = loadPackages(ownerType, context, get())
                val packageVersionMap = loadVersions(packages, ownerType, get())
                coroutineScope {
                    val deletePackagesJob = async {
                        val packagesToDelete =
                            filterPackages(packageVersionMap, context.versionTag, context.isVersionTagStrict, get())
                        val deletedPackages = deletePackages(packagesToDelete, ownerType, get())
                        info("packages deleted: ${deletedPackages.joinToString(separator = ", ").ifEmpty { "none" }}")
                    }
                    val deleteVersionsJob = async {
                        val versionsToDelete =
                            filterVersions(packageVersionMap, context.versionTag, context.isVersionTagStrict, get())
                        val deletedVersions = deleteVersions(versionsToDelete, ownerType, get())
                        info("versions deleted: ${deletedVersions.joinToString(separator = ", ").ifEmpty { "none" }}")
                    }
                    listOf(
                        deletePackagesJob,
                        deleteVersionsJob,
                    ).awaitAll()
                }
            }.fold(
                onSuccess = { exitProcess(0) },
                onFailure = {
                    it.printStackTrace()
                    exitProcess(1)
                },
            )
        }
    }
}

context (Logger)
private suspend fun loadOwnerType(
    context: Context,
    loadRepositoryOwnerTypeUseCase: LoadRepositoryOwnerTypeUseCase,
): OwnerType {
    val params = LoadRepositoryOwnerTypeUseCase.Params(
        owner = context.owner,
        repository = context.repository,
    )
    loadRepositoryOwnerTypeUseCase(params).fold(
        onSuccess = { return it },
        onFailure = {
            error("Failed to check repository owner")
            throw it
        },
    )
}

context (Logger)
private suspend fun loadPackages(
    ownerType: OwnerType,
    context: Context,
    loadPackagesUseCase: LoadPackagesUseCase,
): Collection<Package> {
    val params = LoadPackagesUseCase.Params(
        ownerType = ownerType,
        owner = context.owner,
        repository = context.repository,
        packageType = context.packageType,
    )
    return loadPackagesUseCase(params).fold(
        onSuccess = { it },
        onFailure = {
            error("Failed to load packages")
            throw it
        },
    )
}

context (Logger)
private suspend fun loadVersions(
    packages: Collection<Package>,
    ownerType: OwnerType,
    loadVersionsUseCase: LoadVersionsUseCase,
): Map<Package, Collection<Version>> {
    return runCatching {
        packages.associate { p ->
            val params = LoadVersionsUseCase.Params(
                ownerType = ownerType,
                owner = p.owner.login,
                packageName = p.name,
                packageType = p.packageType,
            )
            loadVersionsUseCase(params).fold(
                onSuccess = { p to it },
                onFailure = { throw it },
            )
        }
    }.fold(
        onSuccess = { it },
        onFailure = {
            error("Failed to load versions")
            throw it
        },
    )
}

private suspend fun filterPackages(
    packageVersionMap: Map<Package, Collection<Version>>,
    versionTag: String,
    isVersionStrict: Boolean,
    filterPackagesUseCase: FilterPackagesUseCase,
): Collection<Package> {
    val params = FilterPackagesUseCase.Params(
        versionTag = versionTag,
        isVersionTagStrict = isVersionStrict,
        data = packageVersionMap,
    )
    return filterPackagesUseCase(params)
}

context (Logger)
private suspend fun deletePackages(
    data: Collection<Package>,
    ownerType: OwnerType,
    deletePackagesUseCase: DeletePackagesUseCase,
): Collection<String> {
    val params = DeletePackagesUseCase.Params(
        ownerType = ownerType,
        packages = data,
    )
    return deletePackagesUseCase(params).fold(
        onSuccess = { it },
        onFailure = {
            error("Failed to delete packages")
            throw it
        },
    )
}

private suspend fun filterVersions(
    packageVersionMap: Map<Package, Collection<Version>>,
    versionTag: String,
    isVersionStrict: Boolean,
    filterVersionsUseCase: FilterVersionsUseCase,
): Collection<PackageVersions> {
    val params = FilterVersionsUseCase.Params(
        versionTag = versionTag,
        isVersionTagStrict = isVersionStrict,
        data = packageVersionMap,
    )
    return filterVersionsUseCase(params)
}

context (Logger)
private suspend fun deleteVersions(
    data: Collection<PackageVersions>,
    ownerType: OwnerType,
    deleteVersionsUseCase: DeleteVersionsUseCase,
): Collection<String> {
    val params = DeleteVersionsUseCase.Params(
        ownerType = ownerType,
        data = data,
    )
    return deleteVersionsUseCase(params).fold(
        onSuccess = { ids ->
            data.map { item ->
                "${item.packageName}:${item.versionIds.intersect(ids.toSet())}"
            }
        },
        onFailure = {
            error("Failed to delete versions")
            throw it
        },
    )
}
