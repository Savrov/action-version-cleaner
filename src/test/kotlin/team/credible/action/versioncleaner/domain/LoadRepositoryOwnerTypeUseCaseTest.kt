package team.credible.action.versioncleaner.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import team.credible.action.versioncleaner.model.OwnerType
import team.credible.action.versioncleaner.model.Repository

class LoadRepositoryOwnerTypeUseCaseTest : BehaviorSpec({
    val repositoryRepository = mockk<RepositoryRepository>()
    val useCase = LoadRepositoryOwnerTypeUseCase(repositoryRepository)
    Given("input") {
        val input = mockk<LoadRepositoryOwnerTypeUseCase.Params>(relaxed = true)
        When("repositoryRepository.loadRepository returns User") {
            coEvery { repositoryRepository.loadRepository(any(), any()) } returns Result.success(
                mockk<Repository>(
                    block = { every { owner.type } returns "User" },
                ),
            )
            Then("returns success") {
                useCase(input) shouldBeSuccess OwnerType.User
                coVerify(exactly = 1) {
                    repositoryRepository.loadRepository(any(), any())
                }
            }
        }
        When("repositoryRepository.loadRepository returns Organization") {
            coEvery { repositoryRepository.loadRepository(any(), any()) } returns Result.success(
                mockk<Repository>(
                    block = { every { owner.type } returns "Organisation" },
                ),
            )
            Then("returns success") {
                useCase(input) shouldBeSuccess OwnerType.Organisation
                coVerify(exactly = 1) {
                    repositoryRepository.loadRepository(any(), any())
                }
            }
        }
        When("repositoryRepository.loadRepository returns invalid value") {
            val exception = Exception()
            coEvery { repositoryRepository.loadRepository(any(), any()) } returns Result.failure(exception)
            Then("returns failure") {
                useCase(input) shouldBeFailure exception
                coVerify(exactly = 1) {
                    repositoryRepository.loadRepository(any(), any())
                }
            }
        }
    }
})
