package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Version

internal class LoadOrganizationPackageVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<LoadOrganizationPackageVersionsUseCase.Params, Result<Collection<Version>>> {

    override suspend fun invoke(input: Params): Result<Collection<Version>> {
        return versionRepository.loadOrganizationVersions(
            organization = input.organization,
            packageName = input.packageName,
            packageType = input.packageType,
        )
    }

    data class Params(
        val organization: String,
        val packageName: String,
        val packageType: String,
    )
}
