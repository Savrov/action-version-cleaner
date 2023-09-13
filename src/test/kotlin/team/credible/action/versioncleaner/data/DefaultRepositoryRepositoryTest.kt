package team.credible.action.versioncleaner.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import team.credible.action.versioncleaner.model.Repository

class DefaultRepositoryRepositoryTest : StringSpec({

    val repositoryDataSource = mockk<RepositoryDataSource>()
    val owner = "credible-team"
    val repo = "version-cleaner"

    """when dataSource loadRepository returns failure result, 
        then repository loadRepository should return failure result""" {
        val repository = DefaultRepositoryRepository(
            repositoryDataSource = repositoryDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.failure<Repository>(Exception("test"))
        coEvery { repositoryDataSource.loadRepository(owner, repo) } returns expected
        val actual = repository.loadRepository(owner, repo)
        actual shouldBe expected
        coVerify { repositoryDataSource.loadRepository(any(), any()) }
    }

    """when dataSource loadRepository returns success result,
        then repository loadRepository should return success result""" {
        val repository = DefaultRepositoryRepository(
            repositoryDataSource = repositoryDataSource,
            coroutineContext = coroutineContext,
        )
        val expected = Result.success<Repository>(mockk())
        coEvery { repositoryDataSource.loadRepository(owner, repo) } returns expected
        val actual = repository.loadRepository(owner, repo)
        actual shouldBe expected
        coVerify { repositoryDataSource.loadRepository(any(), any()) }
    }
})
