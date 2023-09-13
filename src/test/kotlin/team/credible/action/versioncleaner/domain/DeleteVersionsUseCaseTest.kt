package team.credible.action.versioncleaner.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import team.credible.action.versioncleaner.model.ExceptionsBundle
import team.credible.action.versioncleaner.model.OwnerType

class DeleteVersionsUseCaseTest : BehaviorSpec({
    val versionRepository = mockk<VersionRepository>()
    val params = mockk<DeleteVersionsUseCase.Params>()
    val useCase = DeleteVersionsUseCase(versionRepository)
    Given("input.ownerType is User") {
        every { params.ownerType } returns OwnerType.User
        And("input.data is empty") {
            every { params.data } returns listOf()
            Then("result is success") {
                useCase(params) shouldBeSuccess listOf()
                coVerify(exactly = 0) { versionRepository.deleteUserVersions(any(), any(), any(), any()) }
            }
        }
        And("input.data is not empty") {
            every { params.data } returns listOf(mockk(relaxed = true), mockk(relaxed = true))
            When("versionRepository.deleteUserVersions returns collection of success results") {
                coEvery { versionRepository.deleteUserVersions(any(), any(), any(), any()) } returns listOf(
                    Result.success(1),
                    Result.success(2),
                ) andThen listOf(
                    Result.success(3),
                    Result.success(4),
                )
                Then("result is success") {
                    useCase(params) shouldBeSuccess listOf(1, 2, 3, 4)
                    coVerify(exactly = 2) { versionRepository.deleteUserVersions(any(), any(), any(), any()) }
                }
            }
            When("versionRepository.deleteUserVersions returns collection of failure results") {
                val exception = Exception()
                coEvery { versionRepository.deleteUserVersions(any(), any(), any(), any()) } returns listOf(
                    Result.failure(exception),
                    Result.failure(exception),
                ) andThen listOf(
                    Result.failure(exception),
                    Result.failure(exception),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception, exception, exception, exception))
                    coVerify(exactly = 2) { versionRepository.deleteUserVersions(any(), any(), any(), any()) }
                }
            }
            When("versionRepository.deleteUserVersions returns collection of mixed results") {
                val exception = Exception()
                coEvery { versionRepository.deleteUserVersions(any(), any(), any(), any()) } returns listOf(
                    Result.success(1),
                    Result.failure(exception),
                ) andThen listOf(
                    Result.failure(exception),
                    Result.success(4),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception, exception))
                    coVerify(exactly = 2) { versionRepository.deleteUserVersions(any(), any(), any(), any()) }
                }
            }
        }
    }
    Given("input.ownerType is Organisation") {
        every { params.ownerType } returns OwnerType.Organisation
        And("input.data is empty") {
            every { params.data } returns listOf()
            Then("result is success") {
                useCase(params) shouldBeSuccess listOf()
                coVerify(exactly = 0) { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) }
            }
        }
        And("input.data is not empty") {
            every { params.data } returns listOf(mockk(relaxed = true), mockk(relaxed = true))
            When("versionRepository.deleteUserVersions returns collection of success results") {
                coEvery { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) } returns listOf(
                    Result.success(1),
                    Result.success(2),
                ) andThen listOf(
                    Result.success(3),
                    Result.success(4),
                )
                Then("result is success") {
                    useCase(params) shouldBeSuccess listOf(1, 2, 3, 4)
                    coVerify(exactly = 2) { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) }
                }
            }
            When("versionRepository.deleteUserVersions returns collection of failure results") {
                val exception = Exception()
                coEvery { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) } returns listOf(
                    Result.failure(exception),
                    Result.failure(exception),
                ) andThen listOf(
                    Result.failure(exception),
                    Result.failure(exception),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception, exception, exception, exception))
                    coVerify(exactly = 2) { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) }
                }
            }
            When("versionRepository.deleteUserVersions returns collection of mixed results") {
                val exception = Exception()
                coEvery { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) } returns listOf(
                    Result.success(1),
                    Result.failure(exception),
                ) andThen listOf(
                    Result.failure(exception),
                    Result.success(4),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception, exception))
                    coVerify(exactly = 2) { versionRepository.deleteOrganisationVersions(any(), any(), any(), any()) }
                }
            }
        }
    }
})
