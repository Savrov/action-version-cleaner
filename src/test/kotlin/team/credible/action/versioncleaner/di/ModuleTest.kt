package team.credible.action.versioncleaner.di

import io.kotest.core.spec.style.StringSpec
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

class ModuleTest : StringSpec({

    "test koin configuration" {
        koinApplication {
            modules(module)
            checkModules {
                withProperty("GITHUB_REPOSITORY", "credible-team/version-cleaner")
                withProperty("PACKAGE_TYPE", "maven")
                withProperty("PACKAGE_NAME", "package")
                withProperty("VERSION_TAG", "SNAPSHOT")
                withProperty("IS_VERSION_TAG_STRICT", "false")
            }
        }
    }
})
