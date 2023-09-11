package team.credible.action.versioncleaner.system

import team.credible.action.versioncleaner.model.Context
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version

abstract class AbstractFlow {

    suspend operator fun invoke(context: Context) = with(context) {
        val packages = loadPackages().fold(
            onSuccess = { it },
            onFailure = { throw it },
        )
        val packageVersionMap = loadPackageVersions(packages).fold(
            onSuccess = { it },
            onFailure = { throw it },
        )
        deletePackages(packageVersionMap).fold(
            onSuccess = {
                val deletedPackages = it.joinToString(separator = ",").ifEmpty { "none" }
                println("packages deleted: $deletedPackages")
            },
            onFailure = { throw it },
        )
        deleteVersions(packageVersionMap).fold(
            onSuccess = {
                val deletedVersions = it.joinToString(separator = ",") { id ->
                    packageVersionMap.flatMap { entry ->
                        entry.value.filter { it.id == id }.map { "${entry.key.name}#${it.id}" }
                    }.toString()
                }.ifEmpty { "none" }
                println("versions deleted: $deletedVersions")
            },
            onFailure = { throw it },
        )
    }

    context(Context)
    abstract suspend fun loadPackages(): Result<Collection<Package>>

    context(Context)
    abstract suspend fun loadPackageVersions(packages: Collection<Package>): Result<Map<Package, Collection<Version>>>

    context(Context)
    abstract suspend fun deletePackages(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<String>>

    context(Context)
    abstract suspend fun deleteVersions(packageVersionMap: Map<Package, Collection<Version>>): Result<Collection<Int>>
}
