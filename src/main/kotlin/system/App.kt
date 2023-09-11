package system

import di.module
import kotlinx.coroutines.runBlocking
import model.Context
import org.koin.core.context.startKoin
import org.koin.environmentProperties

fun main() = runBlocking {
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }

//    val (organization, repository) = koinApp.koin.getProperty<String>("GITHUB_REPOSITORY")?.split("/")
//        ?: error("environment variable GITHUB_REPOSITORY is missing")
//    val packageType = koinApp.koin.getProperty<String>("PACKAGE_TYPE")
//        ?: error("environment variable PACKAGE_TYPE is missing")


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