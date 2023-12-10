package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.model.Package
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class DefaultPackageRepositoryTest : StringSpec({

    val packageDataSource = mockk<PackageDataSource>()
    val owner = "credible-team"
    val packageType = "maven"

    """when dataSource loadOrganisationPackages returns failure result, 
        then repository loadOrganisationPackages should return failure result""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.failure<Collection<Package>>(Exception("test"))
        coEvery { packageDataSource.loadOrganisationPackages(owner, packageType) } returns expected
        val actual = repository.loadOrganisationPackages(owner, packageType)
        actual shouldBe expected
        coVerify { packageDataSource.loadOrganisationPackages(any(), any()) }
    }

    """when dataSource loadOrganisationPackages returns success result,
        then repository loadOrganisationPackages should return success result""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.success<Collection<Package>>(mockk())
        coEvery { packageDataSource.loadOrganisationPackages(owner, packageType) } returns expected
        val actual = repository.loadOrganisationPackages(owner, packageType)
        actual shouldBe expected
        coVerify { packageDataSource.loadOrganisationPackages(any(), any()) }
    }

    """when all jobs return success result,
        the repository deleteOrganisationPackages should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { index -> Result.success("package#$index") }
        coEvery {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        }
    }

    """when some jobs return success result,
        and other jobs return failure result,
        the repository deleteOrganisationPackages should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { index ->
            if (index % 2 == 0) {
                Result.failure(Exception("test"))
            } else {
                Result.success("package#$index")
            }
        }
        coEvery {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        }
    }

    """when all jobs return failure result,
        the repository deleteOrganisationPackages should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { _ -> Result.failure<String>(Exception("test")) }
        coEvery {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteOrganisationPackage(any(), any(), any())
        }
    }

    """when dataSource loadUserPackages returns failure result, 
        then repository loadUserPackages should return failure result""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.failure<Collection<Package>>(Exception("test"))
        coEvery { packageDataSource.loadUserPackages(owner, packageType) } returns expected
        val actual = repository.loadUserPackages(owner, packageType)
        actual shouldBe expected
        coVerify { packageDataSource.loadUserPackages(any(), any()) }
    }

    """when dataSource loadUserPackages returns success result,
        then repository loadUserPackages should return success result""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.success<Collection<Package>>(mockk())
        coEvery { packageDataSource.loadUserPackages(owner, packageType) } returns expected
        val actual = repository.loadUserPackages(owner, packageType)
        actual shouldBe expected
        coVerify { packageDataSource.loadUserPackages(any(), any()) }
    }

    """when all jobs return success result,
        the repository deleteUserPackages should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { index -> Result.success("$index") }
        coEvery {
            packageDataSource.deleteUserPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteUserPackage(any(), any(), any())
        }
    }

    """when some jobs return success result,
        and other jobs return failure result,
        the repository deleteUserPackage should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { index ->
            if (index % 2 == 0) {
                Result.failure(Exception("test"))
            } else {
                Result.success("package#$index")
            }
        }
        coEvery {
            packageDataSource.deleteUserPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteUserPackage(any(), any(), any())
        }
    }

    """when all jobs return failure result,
        the repository deleteUserPackages should return these results""" {
        val repository = DefaultPackageRepository(
            packageDataSource = packageDataSource,
            coroutineContext = coroutineContext,
        )
        val packages = listOf<Package>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(packages.size) { _ -> Result.failure<String>(Exception("test")) }
        coEvery {
            packageDataSource.deleteUserPackage(any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserPackages(packages)
        actual shouldBe expected
        coVerify(exactly = 2) {
            packageDataSource.deleteUserPackage(any(), any(), any())
        }
    }
})
