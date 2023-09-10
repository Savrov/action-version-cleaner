package data

import domain.VersionRepository
import kotlinx.coroutines.withContext
import model.PackageVersion
import kotlin.coroutines.CoroutineContext

internal class DefaultVersionRepository(
    private val versionDataSource: VersionDataSource,
    private val coroutineContext: CoroutineContext,
) : VersionRepository {

    override suspend fun getVersions(
        organization: String,
        packageName: String,
        packageType: String
    ): Result<Collection<PackageVersion>> {
        return withContext(coroutineContext) {
            val versions = mutableListOf<PackageVersion>()
            var isNextPageAvailable = false
            var page = 0
            do {
                page++
                versionDataSource.getVersions(
                    organization = organization,
                    packageName = packageName,
                    packageType = packageType,
                    page = page
                ).fold(
                    onSuccess = {
                        versions.addAll(it)
                        isNextPageAvailable = it.isEmpty().not()
                    },
                    onFailure = {
                        return@withContext Result.failure<Collection<PackageVersion>>(it)
                    }
                )
            } while (isNextPageAvailable)
            Result.success(versions)
        }
    }
}