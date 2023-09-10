fun main() {
    val token = System.getenv("PACKAGES_TOKEN").also {
        println("token: $it")
    }
    val organization = System.getenv("GITHUB_REPOSITORY_OWNER").also {
        println("organization: $it")
    }
    val repository = System.getenv("GITHUB_REPOSITORY").also {
        println("repository: $it")
    }
}