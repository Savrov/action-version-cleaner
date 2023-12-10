package com.github.savrov.github.action.versioncleaner.domain

import com.github.savrov.github.action.versioncleaner.model.Package
import com.github.savrov.github.action.versioncleaner.model.Version

internal class FilterPackagesUseCase : SuspendUseCase<FilterPackagesUseCase.Params, Collection<Package>> {

    override suspend fun invoke(input: Params): Collection<Package> {
        return input.data.mapValues { entry ->
            entry.value.filter {
                if (input.isVersionTagStrict) {
                    it.name == input.versionTag
                } else {
                    it.name.contains(input.versionTag)
                }
            }
        }.filterValues { it.count() == 1 }
            .map {
                it.key
            }
    }

    data class Params(
        val versionTag: String,
        val isVersionTagStrict: Boolean,
        val data: Map<Package, Collection<Version>>,
    )
}
