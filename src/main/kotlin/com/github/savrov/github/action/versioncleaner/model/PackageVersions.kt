package com.github.savrov.github.action.versioncleaner.model

data class PackageVersions(
    val owner: String,
    val packageName: String,
    val packageType: String,
    val versionIds: Collection<Int>,
)
