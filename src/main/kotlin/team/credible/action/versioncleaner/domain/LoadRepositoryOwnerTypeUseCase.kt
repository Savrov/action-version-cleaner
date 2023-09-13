package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.OwnerType

internal class LoadRepositoryOwnerTypeUseCase(
    private val repositoryRepository: RepositoryRepository,
) : SuspendUseCase<LoadRepositoryOwnerTypeUseCase.Params, Result<OwnerType>> {

    override suspend fun invoke(input: Params): Result<OwnerType> {
        return runCatching {
            repositoryRepository.loadRepository(
                owner = input.owner,
                repository = input.repository,
            ).map { repository ->
                when (repository.owner.type.lowercase()) {
                    "user" -> OwnerType.User
                    "organisation", "organization" -> OwnerType.Organisation
                    else -> error("unsupported owner type: ${repository.owner.type}")
                }
            }
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) },
        )
    }

    data class Params(
        val owner: String,
        val repository: String,
    )
}
