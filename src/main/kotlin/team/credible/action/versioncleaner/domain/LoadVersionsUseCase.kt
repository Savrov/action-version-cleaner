package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Version

internal class LoadVersionsUseCase(
    private val versionRepository: VersionRepository,
) : SuspendUseCase<LoadVersionsUseCase.Params, Result<Collection<Version>>> {

    override suspend fun invoke(input: Params): Result<Collection<Version>> {
        return when (input.ownerType) {
            OwnerType.User -> versionRepository.loadUserVersions(
                user = input.owner,
                packageName = input.packageName,
                packageType = input.packageType,
            )

            OwnerType.Organisation -> versionRepository.loadOrganisationVersions(
                organisation = input.owner,
                packageName = input.packageName,
                packageType = input.packageType,
            )
        }
    }

    data class Params(
        val ownerType: OwnerType,
        val owner: String,
        val packageName: String,
        val packageType: String,
    )
}
