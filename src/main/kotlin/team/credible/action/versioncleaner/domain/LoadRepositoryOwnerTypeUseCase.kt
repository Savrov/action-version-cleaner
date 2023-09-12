package team.credible.action.versioncleaner.domain

import team.credible.action.versioncleaner.model.OwnerType

internal class LoadRepositoryOwnerTypeUseCase(
    private val repositoryRepository: RepositoryRepository,
) : SuspendUseCase<LoadRepositoryOwnerTypeUseCase.Params, OwnerType> {

    override suspend fun invoke(input: Params): Result<OwnerType> {
        return repositoryRepository.loadRepository(
            owner = input.owner,
            repository = input.repository,
        ).map { repository ->
            if (repository.owner.type == "User") {
                OwnerType.User
            } else {
                OwnerType.Organisation
            }
        }
    }

    data class Params(
        val owner: String,
        val repository: String,
    )
}
