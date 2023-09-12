package team.credible.action.versioncleaner.system

import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import team.credible.action.versioncleaner.di.module
import team.credible.action.versioncleaner.domain.DeletePackagesUseCase
import team.credible.action.versioncleaner.domain.DeleteVersionsUseCase
import team.credible.action.versioncleaner.domain.LoadPackagesUseCase
import team.credible.action.versioncleaner.domain.LoadRepositoryOwnerTypeUseCase
import team.credible.action.versioncleaner.domain.LoadVersionsUseCase
import team.credible.action.versioncleaner.model.Context
import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version
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
                val deletedPackages = deletePackages(packageVersionMap, context.versionTag, ownerType, get())
                info("packages deleted: ${deletedPackages.joinToString(separator = ", ").ifEmpty { "none" }}")
                val deletedVersions =
                    deleteVersions(packageVersionMap, context.versionTag, context.isVersionTagStrict, ownerType, get())
                info("versions deleted: ${deletedVersions.joinToString(separator = ", ").ifEmpty { "none" }}")
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

context (Logger)
private suspend fun deletePackages(
    packageVersionMap: Map<Package, Collection<Version>>,
    versionTag: String,
    ownerType: OwnerType,
    deletePackagesUseCase: DeletePackagesUseCase,
): Collection<String> {
    val dataToDelete = packageVersionMap.filterValues {
        it.count() == 1 && it.all { it.name.contains(versionTag) }
    }
    val params = DeletePackagesUseCase.Params(
        ownerType = ownerType,
        packages = dataToDelete.keys,
    )
    return deletePackagesUseCase(params).fold(
        onSuccess = { it },
        onFailure = {
            error("Failed to delete packages")
            throw it
        },
    )
}

context (Logger)
private suspend fun deleteVersions(
    packageVersionMap: Map<Package, Collection<Version>>,
    versionTag: String,
    isVersionStrict: Boolean,
    ownerType: OwnerType,
    deleteVersionsUseCase: DeleteVersionsUseCase,
): Collection<String> {
    val dataToDelete = packageVersionMap.filterValues {
        it.count() > 1 && it.all { it.name.contains(versionTag) }
    }
    val params = DeleteVersionsUseCase.Params(
        ownerType = ownerType,
        versionTag = versionTag,
        isVersionTagStrict = isVersionStrict,
        data = dataToDelete,
    )
    return deleteVersionsUseCase(params).fold(
        onSuccess = {
            it.map { id ->
                packageVersionMap.flatMap { entry ->
                    entry.value.filter { it.id == id }.map { "${entry.key.name}#${it.id}" }
                }.toString()
            }
        },
        onFailure = {
            error("Failed to delete versions")
            throw it
        },
    )
}
