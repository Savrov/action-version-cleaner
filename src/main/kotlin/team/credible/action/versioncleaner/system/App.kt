package team.credible.action.versioncleaner.system

import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import team.credible.action.versioncleaner.di.module
import team.credible.action.versioncleaner.model.Context

fun main() = runBlocking {
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }
    with(koinApp.koin.get<Context>()) {
        if (organization == null) {
            println("organization is missing. user flow is not supported yet")
            return@runBlocking
        } else {
            koinApp.koin.get<OrganizationFlow>().run {
                invoke(this@with)
            }
        }
    }
}
