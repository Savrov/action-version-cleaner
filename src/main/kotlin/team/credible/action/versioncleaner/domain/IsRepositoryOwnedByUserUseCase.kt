package team.credible.action.versioncleaner.domain

internal class IsRepositoryOwnedByUserUseCase(
    private val repositoryRepository: RepositoryRepository,
) : SuspendUseCase<IsRepositoryOwnedByUserUseCase.Params, Result<Boolean>> {

    override suspend fun invoke(input: Params): Result<Boolean> {
        return repositoryRepository.loadRepository(
            owner = input.owner,
            repository = input.repository,
        ).map { repository ->
            repository.owner.type == "User"
        }
    }

    data class Params(
        val owner: String,
        val repository: String,
    )
}
