package domain

import model.Package

internal class LoadPackagesByRepositoryUseCase(
    private val packageRepository: PackageRepository,
) : SuspendUseCase<LoadPackagesByRepositoryUseCase.Params, Collection<Package>> {

    override suspend fun invoke(input: Params): Result<Collection<Package>> {
        return packageRepository.loadPackages(
            organization = input.organization,
            packageType = input.packageType
        )
//            .also {
//            println("list size BEFORE=${it.getOrNull()?.count()}")
//        }.map { list ->
//            list.filter { item ->
//                item.repository?.name == input.repository
//            }
//        }.also {
//            println("list size AFTER=${it.getOrNull()?.count()}")
//        }
    }

    data class Params(
        val organization: String,
        val repository: String,
        val packageType: String,
    )
}