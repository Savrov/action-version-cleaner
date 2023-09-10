package domain

import model.PackageVersion

internal class LoadPackageVersionsUseCase(
    private val versionRepository: VersionRepository,
): SuspendUseCase<LoadPackageVersionsUseCase.Params, Result<Collection<PackageVersion>>> {

    override suspend fun invoke(input: Params): Result<Collection<PackageVersion>> {
        return versionRepository.getVersions(
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