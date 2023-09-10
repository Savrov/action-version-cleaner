package data

import domain.PackageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import model.Package
import model.PackageVersion
import kotlin.coroutines.CoroutineContext

internal class DefaultPackageRepository(
    private val packageDataSource: PackageDataSource,
    private val coroutineContext: CoroutineContext,
) : PackageRepository {
    override suspend fun loadPackages(
        organization: String,
        packageType: String
    ): Result<Collection<Package>> {
        return withContext(coroutineContext) {
            packageDataSource.loadPackages(organization, packageType)
        }
    }

    override suspend fun deletePackages(data: Collection<Package>): Collection<Result<Unit>> {
        return withContext(coroutineContext) {
            val jobs = data.map {
                async {
                    packageDataSource.deletePackage(
                        organization = it.owner?.login ?: error("organization is missing"),
                        packageName = it.name ?: error("package name is missing"),
                        packageType = it.packageType ?: error("package type is missing")
                    )
                }
            }
            jobs.awaitAll()
        }
    }
}