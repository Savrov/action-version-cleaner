package data

import domain.PackageRepository
import kotlinx.coroutines.withContext
import model.Package
import kotlin.coroutines.CoroutineContext

internal class DefaultPackageRepository(
    private val packageDataSource: PackageDataSource,
    private val coroutineContext: CoroutineContext,
): PackageRepository {
    override suspend fun loadPackages(organization: String, packageType: String): Result<Collection<Package>> {
        return withContext(coroutineContext) {
            packageDataSource.loadPackages(organization, packageType)
        }
    }
}