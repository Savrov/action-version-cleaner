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
    override suspend fun loadOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>> {
        return withContext(coroutineContext) {
            val versions = mutableListOf<Version>()
            var isNextPageAvailable = false
            var page = 0
            do {
                page++
                versionDataSource.getOrganisationVersions(
                    organisation = organisation,
                    packageName = packageName,
                    packageType = packageType,
                    page = page,
                ).fold(
                    onSuccess = {
                        versions.addAll(it)
                        isNextPageAvailable = it.isEmpty().not()
                    },
                    onFailure = {
                        return@withContext Result.failure<Collection<Version>>(it)
                    },
                )
            } while (isNextPageAvailable)
            Result.success(versions)
        }
    }

    override suspend fun deleteOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>> {
        return withContext(coroutineContext) {
            val jobs = versionIds.map {
                async {
                    versionDataSource.deleteOrganisationVersion(
                        versionId = it,
                        organisation = organisation,
                        packageName = packageName,
                        packageType = packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }

    override suspend fun loadUserVersions(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>> {
        return withContext(coroutineContext) {
            versionDataSource.getUserVersions(
                user = user,
                packageName = packageName,
                packageType = packageType,
            )
        }
    }

    override suspend fun deleteUserVersions(
        user: String,
        packageName: String,
        packageType: String,
        versionIds: Collection<Int>,
    ): Collection<Result<Int>> {
        return withContext(coroutineContext) {
            val jobs = versionIds.map {
                async {
                    versionDataSource.deleteUserVersion(
                        versionId = it,
                        user = user,
                        packageName = packageName,
                        packageType = packageType,
                    )
                }
            }
            jobs.awaitAll()
        }
    }
}
