package team.credible.action.versioncleaner.system

import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.environmentProperties
import team.credible.action.versioncleaner.di.module
import team.credible.action.versioncleaner.domain.IsRepositoryOwnedByUserUseCase
import team.credible.action.versioncleaner.model.Context

fun main() = runBlocking {
    val koinApp = startKoin {
        environmentProperties()
        modules(module)
    }
    with(koinApp.koin) {
        val context = get<Context>()
        val isRepositoryOwnedByUser = get<IsRepositoryOwnedByUserUseCase>()
            .invoke(
                IsRepositoryOwnedByUserUseCase.Params(
                    owner = context.owner,
                    repository = context.repository,
                )
            ).fold(
                onSuccess = { it },
                onFailure = { throw it }
            )
        println("isRepositoryOwnedByUser=$isRepositoryOwnedByUser")
        if (isRepositoryOwnedByUser) {
            error("repository belongs to the user. user flow is not supported yet")
        } else {
            get<OrganizationFlow>().run {
                invoke(context)
            }
        }
    }
}
