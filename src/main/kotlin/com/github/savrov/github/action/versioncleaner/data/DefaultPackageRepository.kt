package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.domain.PackageRepository
import com.github.savrov.github.action.versioncleaner.model.Package
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class DefaultPackageRepository(
    private val packageDataSource: PackageDataSource,
    private val coroutineContext: CoroutineContext,
) : PackageRepository {

    override suspend fun loadOrganisationPackages(
        organisation: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return withContext(coroutineContext) {
            packageDataSource.loadOrganisationPackages(organisation, packageType)
        }
    }

    override suspend fun deleteOrganisationPackages(
        data: Collection<Package>,
    ): Collection<Result<String>> {
        return withContext(coroutineContext) {
            val jobs = data.map {
                async {
                    packageDataSource.deleteOrganisationPackage(
                        organisation = it.owner.login,
                        packageName = it.name,
                        packageType = it.packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }

    override suspend fun loadUserPackages(
        user: String,
        packageType: String,
    ): Result<Collection<Package>> {
        return withContext(coroutineContext) {
            packageDataSource.loadUserPackages(user, packageType)
        }
    }

    override suspend fun deleteUserPackages(
        data: Collection<Package>,
    ): Collection<Result<String>> {
        return withContext(coroutineContext) {
            val jobs = data.map {
                async {
                    packageDataSource.deleteUserPackage(
                        user = it.owner.login,
                        packageName = it.name,
                        packageType = it.packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }
}
