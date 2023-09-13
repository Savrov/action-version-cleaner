package team.credible.action.versioncleaner.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import team.credible.action.versioncleaner.model.Owner
import team.credible.action.versioncleaner.model.Package
import team.credible.action.versioncleaner.model.Version

class FilterPackagesUseCaseTest : BehaviorSpec({

    val tag = "SNAPSHOT"
    val data = mapOf(
        Package("package#1", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(0, tag),
            Version(1, tag),
            Version(2, "prefix-$tag"),
        ),
        Package("package#2", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(3, tag),
            Version(4, "$tag-suffix"),
        ),
        Package("package#3", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(5, "random"),
        ),
        Package("package#4", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(6, tag),
        ),
        Package("package#5", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(7, "prefix-$tag"),
        ),
        Package("package#6", "type", Owner("owner", "type"), mockk()) to listOf(
            Version(8, "$tag-suffix"),
        ),
    )
    val params = mockk<FilterPackagesUseCase.Params>()
    val useCase = FilterPackagesUseCase()

    Given("input.data & input.versionTag") {
        every { params.data } returns data
        every { params.versionTag } returns tag
        And("input.isVersionTagStrict is true") {
            every { params.isVersionTagStrict } returns true
            Then("output should be filtered by strict tag") {
                val output = useCase(params)
                output shouldHaveSize 2
                output shouldContain data.filterKeys { it.name == "package#2" }.keys.single()
                output shouldContain data.filterKeys { it.name == "package#4" }.keys.single()
            }
        }
        And("input.isVersionTagStrict is false") {
            every { params.isVersionTagStrict } returns false
            Then("output should be filtered by partial tag") {
                val output = useCase(params)
                output shouldHaveSize 3
                output shouldContain data.filterKeys { it.name == "package#4" }.keys.single()
                output shouldContain data.filterKeys { it.name == "package#5" }.keys.single()
                output shouldContain data.filterKeys { it.name == "package#6" }.keys.single()
            }
        }
    }
})
