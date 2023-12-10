package com.github.savrov.github.action.versioncleaner.domain

import com.github.savrov.github.action.versioncleaner.model.OwnerType
import com.github.savrov.github.action.versioncleaner.model.Version
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

class LoadVersionsUseCaseTest : BehaviorSpec({
    val versionRepository = mockk<VersionRepository>()
    val params = mockk<LoadVersionsUseCase.Params>(relaxed = true)
    val useCase = LoadVersionsUseCase(versionRepository)
    Given("input.ownerType is User") {
        every { params.ownerType } returns OwnerType.User
        When("versionRepository.loadUserVersions returns success") {
            val data = listOf<Version>(mockk(), mockk())
            coEvery { versionRepository.loadUserVersions(any(), any(), any()) } returns Result.success(data)
            Then("returns success") {
                useCase(params) shouldBeSuccess data
                coVerify(exactly = 1) {
                    versionRepository.loadUserVersions(any(), any(), any())
                }
            }
        }
        When("versionRepository.loadUserVersions returns failure") {
            val exception = Exception()
            coEvery { versionRepository.loadUserVersions(any(), any(), any()) } returns Result.failure(exception)
            Then("returns failure") {
                useCase(params) shouldBeFailure exception
                coVerify(exactly = 1) {
                    versionRepository.loadUserVersions(any(), any(), any())
                }
            }
        }
    }
    Given("input.ownerType is Organisation") {
        every { params.ownerType } returns OwnerType.Organisation
        When("versionRepository.loadOrganisationVersions returns success") {
            val data = listOf<Version>(mockk(), mockk())
            coEvery { versionRepository.loadOrganisationVersions(any(), any(), any()) } returns Result.success(data)
            Then("returns success") {
                useCase(params) shouldBeSuccess data
                coVerify(exactly = 1) {
                    versionRepository.loadOrganisationVersions(any(), any(), any())
                }
            }
        }
        When("versionRepository.loadOrganisationVersions returns failure") {
            val exception = Exception()
            coEvery {
                versionRepository.loadOrganisationVersions(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.failure(exception)
            Then("returns failure") {
                useCase(params) shouldBeFailure exception
                coVerify(exactly = 1) {
                    versionRepository.loadOrganisationVersions(any(), any(), any())
                }
            }
        }
    }
})
