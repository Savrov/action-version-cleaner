package com.github.savrov.github.action.versioncleaner.data

import com.github.savrov.github.action.versioncleaner.model.Version
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class DefaultVersionRepositoryTest : StringSpec({

    val versionDataSource = mockk<VersionDataSource>()
    val owner = "credible-team"
    val packageType = "maven"
    val packageName = "package"

    """when dataSource getOrganisationVersions returns failure result, 
        then repository loadOrganisationVersions should return failure result""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.failure<Collection<Version>>(Exception("test"))
        coEvery { versionDataSource.getOrganisationVersions(owner, packageName, packageType, any()) } returns expected
        val actual = repository.loadOrganisationVersions(owner, packageName, packageType)
        actual shouldBe expected
        coVerify { versionDataSource.getOrganisationVersions(any(), any(), any(), any()) }
    }

    """when dataSource getOrganisationVersions returns success result,
        then repository loadOrganisationVersions should return success result""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.success<Collection<Version>>(listOf(mockk(), mockk()))
        coEvery {
            versionDataSource.getOrganisationVersions(owner, packageName, packageType, any())
        } returns expected andThen Result.success(emptyList())
        val actual = repository.loadOrganisationVersions(owner, packageName, packageType)
        actual shouldBe expected
        coVerify { versionDataSource.getOrganisationVersions(any(), any(), any(), any()) }
    }

    """when all jobs return success result,
        the repository deleteOrganisationVersions should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { index -> Result.success(index) }
        coEvery {
            versionDataSource.deleteOrganisationVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) {
            versionDataSource.deleteOrganisationVersion(any(), any(), any(), any())
        }
    }

    """when some jobs return success result,
        and other jobs return failure result,
        the repository deleteOrganisationVersions should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { index ->
            if (index % 2 == 0) {
                Result.failure(Exception("test"))
            } else {
                Result.success(index)
            }
        }
        coEvery {
            versionDataSource.deleteOrganisationVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) { versionDataSource.deleteOrganisationVersion(any(), any(), any(), any()) }
    }

    """when all jobs return failure result,
        the repository deleteOrganisationVersions should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { _ -> Result.failure<Int>(Exception("test")) }
        coEvery {
            versionDataSource.deleteOrganisationVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteOrganisationVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) { versionDataSource.deleteOrganisationVersion(any(), any(), any(), any()) }
    }

    """when dataSource loadUserVersions returns failure result, 
        then repository loadUserVersions should return failure result""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.failure<Collection<Version>>(Exception("test"))
        coEvery { versionDataSource.getUserVersions(owner, packageName, packageType) } returns expected
        val actual = repository.loadUserVersions(owner, packageName, packageType)
        actual shouldBe expected
        coVerify { versionDataSource.getUserVersions(any(), any(), any()) }
    }

    """when dataSource loadUserVersions returns success result,
        then repository loadUserVersions should return success result""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.success<Collection<Version>>(mockk())
        coEvery { versionDataSource.getUserVersions(owner, packageName, packageType) } returns expected
        val actual = repository.loadUserVersions(owner, packageName, packageType)
        actual shouldBe expected
        coVerify { versionDataSource.getUserVersions(any(), any(), any()) }
    }

    """when all jobs return success result,
        the repository deleteUserPackages should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { index -> Result.success(index) }
        coEvery {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        }
    }

    """when some jobs return success result,
        and other jobs return failure result,
        the repository deleteUserPackage should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { index ->
            if (index % 2 == 0) {
                Result.failure(Exception("test"))
            } else {
                Result.success(index)
            }
        }
        coEvery {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        }
    }

    """when all jobs return failure result,
        the repository deleteUserPackages should return these results""" {
        val repository = DefaultVersionRepository(
            versionDataSource = versionDataSource,
            coroutineContext = coroutineContext,
        )
        val versions = listOf<Version>(mockk(relaxed = true), mockk(relaxed = true))
        val expected = List(versions.size) { _ -> Result.failure<Int>(Exception("test")) }
        coEvery {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        } returns expected[0] andThen expected[1]
        val actual = repository.deleteUserVersions(owner, packageName, packageType, versions.map { it.id })
        actual shouldBe expected
        coVerify(exactly = 2) {
            versionDataSource.deleteUserVersion(any(), any(), any(), any())
        }
    }
})
