package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.PackageVersions
import team.credible.action.versioncleaner.model.Version

internal class FilterVersionsUseCase : SuspendUseCase<FilterVersionsUseCase.Params, Collection<PackageVersions>> {
    override suspend fun invoke(input: Params): Collection<PackageVersions> {
        return input.data.mapValues { entry ->
            entry.value.filter {
                if (input.isVersionTagStrict) {
                    it.name == input.versionTag
                } else {
                    it.name.contains(input.versionTag)
                }
            }
        }.filterValues { it.count() > 1 }
            .map {
                PackageVersions(
                    owner = it.key.owner.login,
                    packageName = it.key.name,
                    packageType = it.key.packageType,
                    versionIds = it.value.map { it.id },
                )
            }
    }

    data class Params(
        val versionTag: String,
        val isVersionTagStrict: Boolean,
        val data: Map<Package, Collection<Version>>,
    )
}
