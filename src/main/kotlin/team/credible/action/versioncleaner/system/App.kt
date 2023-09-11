package team.credible.action.versioncleaner.system

import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import org.slf4j.LoggerFactory
import team.credible.action.versioncleaner.di.module
import team.credible.action.versioncleaner.domain.IsRepositoryOwnedByUserUseCase
import team.credible.action.versioncleaner.model.Context

fun main() = runBlocking {
    val logger = LoggerFactory.getLogger("main")
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }
    with(logger) {
        with(koinApp.koin) {
            val context = get<Context>()
            val isRepositoryOwnedByUser = get<IsRepositoryOwnedByUserUseCase>()
                .invoke(
                    IsRepositoryOwnedByUserUseCase.Params(
                        owner = context.owner,
                        repository = context.repository,
                    ),
                ).fold(
                    onSuccess = { it },
                    onFailure = {
                        error("Failed to check repository owner")
                        throw it
                    },
                )
            logger.info("repository owner: ${if (isRepositoryOwnedByUser) "user" else "organization"}")
            if (isRepositoryOwnedByUser) {
                error("Repository belongs to the user. user flow is not supported yet")
            } else {
                get<OrganizationFlow>().run {
                    invoke(context)
                }
            }
        }
    }
}
