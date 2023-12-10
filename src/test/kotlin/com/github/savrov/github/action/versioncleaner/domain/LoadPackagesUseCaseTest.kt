package com.github.savrov.github.action.versioncleaner.domain

import com.github.savrov.github.action.versioncleaner.model.OwnerType
import com.github.savrov.github.action.versioncleaner.model.Package
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

class LoadPackagesUseCaseTest : BehaviorSpec({
    val packageRepository = mockk<PackageRepository>()
    val params = mockk<LoadPackagesUseCase.Params>(block = {
        every { repository } returns "repository"
    }, relaxed = true)
    val useCase = LoadPackagesUseCase(packageRepository)
    Given("input.ownerType is User") {
        every { params.ownerType } returns OwnerType.User
        When("packageRepository.loadUserPackages() returns success result") {
            val actual = mockk<Package>(block = {
                every { repository.name } returns params.repository
            })
            coEvery { packageRepository.loadUserPackages(any(), any()) } returns Result.success(
                listOf(
                    mockk(block = { every { repository.name } returns "test" }),
                    actual,
                ),
            )
            Then("result is success") {
                useCase(params) shouldBeSuccess listOf(actual)
                coVerify(exactly = 1) { packageRepository.loadUserPackages(any(), any()) }
            }
        }
        When("packageRepository.loadUserPackages() returns failure result") {
            val exception = Exception()
            coEvery { packageRepository.loadUserPackages(any(), any()) } returns Result.failure(exception)
            Then("result is failure") {
                useCase(params) shouldBeFailure exception
                coVerify(exactly = 1) { packageRepository.loadUserPackages(any(), any()) }
            }
        }
    }
    Given("input.ownerType is Organisation") {
        every { params.ownerType } returns OwnerType.Organisation
        When("packageRepository.loadOrganisationPackages() returns success result") {
            val actual = mockk<Package>(block = {
                every { repository.name } returns params.repository
            })
            coEvery { packageRepository.loadOrganisationPackages(any(), any()) } returns Result.success(
                listOf(
                    mockk(block = { every { repository.name } returns "test" }),
                    actual,
                ),
            )
            Then("result is success") {
                useCase(params) shouldBeSuccess listOf(actual)
                coVerify(exactly = 1) { packageRepository.loadOrganisationPackages(any(), any()) }
            }
        }
        When("packageRepository.loadOrganisationPackages() returns failure result") {
            val exception = Exception()
            coEvery { packageRepository.loadOrganisationPackages(any(), any()) } returns Result.failure(exception)
            Then("result is failure") {
                useCase(params) shouldBeFailure exception
                coVerify(exactly = 1) { packageRepository.loadOrganisationPackages(any(), any()) }
            }
        }
    }
})
