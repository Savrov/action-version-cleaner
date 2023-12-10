package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.model.Package

interface PackageDataSource {

    suspend fun loadOrganisationPackages(
        organisation: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteOrganisationPackage(
        organisation: String,
        packageName: String,
        packageType: String,
    ): Result<String>

    suspend fun loadUserPackages(
        user: String,
        packageType: String,
    ): Result<Collection<Package>>

    suspend fun deleteUserPackage(
        user: String,
        packageName: String,
        packageType: String,
    ): Result<String>
}
