package system

import model.Context

fun main() {
    val token = System.getenv("TOKEN")
    val organization = System.getenv("GITHUB_REPOSITORY_OWNER")
    val repository = System.getenv("GITHUB_REPOSITORY").split("/")[1]
    val context = Context(
        token = token,
        organization = organization,
        repository = repository
    )
    println("context=$context")
}