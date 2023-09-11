package team.credible.action.versioncleaner.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import team.credible.action.versioncleaner.domain.VersionRepository
import team.credible.action.versioncleaner.model.Version
import kotlin.coroutines.CoroutineContext

internal class DefaultVersionRepository(
    private val versionDataSource: VersionDataSource,
    private val coroutineContext: CoroutineContext,
) : VersionRepository {

    override suspend fun loadVersions(
        organization: String,
        packageName: String,
        packageType: String
    ): Result<Collection<Version>> {
        return withContext(coroutineContext) {
            val versions = mutableListOf<Version>()
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
                        return@withContext Result.failure<Collection<Version>>(it)
                    }
                )
            } while (isNextPageAvailable)
            Result.success(versions)
        }
    }

    override suspend fun deleteVersions(
        organization: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>> {
        return withContext(coroutineContext) {
            val jobs = versionIds.map {
                async {
                    versionDataSource.deleteVersion(
                        versionId = it,
                        organization = organization,
                        packageName = packageName,
                        packageType = packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }
}