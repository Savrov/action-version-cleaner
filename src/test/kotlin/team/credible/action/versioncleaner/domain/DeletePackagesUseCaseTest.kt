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

class DeletePackagesUseCaseTest : BehaviorSpec({
    val packageRepository = mockk<PackageRepository>()
    val params = mockk<DeletePackagesUseCase.Params>()
    val useCase = DeletePackagesUseCase(packageRepository)
    Given("input.packages is empty") {
        every { params.packages } returns emptyList()
        And("ownerType is User") {
            every { params.ownerType } returns OwnerType.User
            And("packageRepository.deleteUserPackages returns empty collection") {
                coEvery { packageRepository.deleteUserPackages(any()) } returns emptyList()
                Then("result is success") {
                    useCase(params) shouldBeSuccess emptyList()
                    coVerify(exactly = 1) { packageRepository.deleteUserPackages(emptyList()) }
                }
            }
        }
        And("ownerType is Organisation") {
            every { params.ownerType } returns OwnerType.Organisation
            And("packageRepository.deleteUserPackages returns empty collection") {
                coEvery { packageRepository.deleteOrganisationPackages(any()) } returns emptyList()
                Then("result is success") {
                    useCase(params) shouldBeSuccess emptyList()
                    coVerify(exactly = 1) { packageRepository.deleteOrganisationPackages(emptyList()) }
                }
            }
        }
    }
    Given("input.packages is not empty") {
        every { params.packages } returns listOf(mockk(), mockk())
        And("ownerType is User") {
            every { params.ownerType } returns OwnerType.User
            When("packageRepository.deleteUserPackages returns collection of success results") {
                coEvery { packageRepository.deleteUserPackages(any()) } returns listOf(Result.success("name"))
                Then("result is success") {
                    useCase(params) shouldBeSuccess listOf("name")
                }
            }
            When("packageRepository.deleteUserPackages returns collection of failure results") {
                val exception = Exception()
                coEvery { packageRepository.deleteUserPackages(any()) } returns listOf(Result.failure(exception))
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception))
                }
            }
            When("packageRepository.deleteUserPackages returns collection of mixed results") {
                val exception = Exception()
                coEvery { packageRepository.deleteUserPackages(any()) } returns listOf(
                    Result.success("name"),
                    Result.failure(exception),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception))
                }
            }
        }
        And("ownerType is Organisation") {
            every { params.ownerType } returns OwnerType.Organisation
            When("packageRepository.deleteUserPackages returns collection of success results") {
                coEvery { packageRepository.deleteOrganisationPackages(any()) } returns listOf(Result.success("name"))
                Then("result is success") {
                    useCase(params) shouldBeSuccess listOf("name")
                }
            }
            When("packageRepository.deleteUserPackages returns collection of failure results") {
                val exception = Exception()
                coEvery { packageRepository.deleteOrganisationPackages(any()) } returns listOf(Result.failure(exception))
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception))
                }
            }
            When("packageRepository.deleteUserPackages returns collection of mixed results") {
                val exception = Exception()
                coEvery { packageRepository.deleteOrganisationPackages(any()) } returns listOf(
                    Result.success("name"),
                    Result.failure(exception),
                )
                Then("result is failure") {
                    useCase(params) shouldBeFailure ExceptionsBundle(listOf(exception))
                }
            }
        }
    }
})
