package team.credible.action.versioncleaner.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import team.credible.action.versioncleaner.model.Owner
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.PackageVersions
import team.credible.action.versioncleaner.model.Version

class FilterVersionsUseCaseTest : BehaviorSpec({

    val tag = "SNAPSHOT"
    val data = mapOf(
        Package("package#1", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(1, tag),
            Version(2, "prefix-$tag"),
            Version(3, tag),
        ),
        Package("package#2", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(4, tag),
            Version(5, "$tag-suffix"),
        ),
        Package("package#3", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(6, "random"),
        ),
    )
    val params = mockk<FilterVersionsUseCase.Params>()
    val useCase = FilterVersionsUseCase()

    Given("input.data & input.versionTag") {
        every { params.data } returns data
        every { params.versionTag } returns tag
        And("input.isVersionTagStrict is true") {
            every { params.isVersionTagStrict } returns true
            Then("output should be filtered by strict tag") {
                val output = useCase(params)
                output shouldHaveSize 1
                output shouldContain PackageVersions(
                    owner = "owner",
                    packageName = "package#1",
                    packageType = "type",
                    versionIds = listOf(1, 3),
                )
            }
        }
        And("input.isVersionTagStrict is false") {
            every { params.isVersionTagStrict } returns false
            Then("output should be filtered by partial tag") {
                val output = useCase(params)
                output shouldHaveSize 2
                output shouldContain PackageVersions(
                    owner = "owner",
                    packageName = "package#1",
                    packageType = "type",
                    versionIds = listOf(1, 2, 3),
                )
                output shouldContain PackageVersions(
                    owner = "owner",
                    packageName = "package#2",
                    packageType = "type",
                    versionIds = listOf(4, 5),
                )
            }
        }
    }
})
