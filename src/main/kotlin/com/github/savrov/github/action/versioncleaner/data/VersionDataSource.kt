package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.model.Version

interface VersionDataSource {

    suspend fun getOrganisationVersions(
        organisation: String,
        packageName: String,
        packageType: String,
        page: Int,
    ): Result<Collection<Version>>

    suspend fun deleteOrganisationVersion(
        versionId: Int,
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<Int>

    suspend fun getUserVersions(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Collection<Version>>

    suspend fun deleteUserVersion(
        versionId: Int,
        user: String,
        packageName: String,
        packageType: String,
    ): Result<Int>
}
