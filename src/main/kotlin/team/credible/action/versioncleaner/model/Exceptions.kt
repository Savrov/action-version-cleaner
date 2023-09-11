package team.credible.action.versioncleaner.model

class ExceptionsBundle(private val exceptions: List<Throwable>) : Exception() {
    override val message: String
        get() = exceptions.joinToString("\n") { it.message ?: "" }
}

class NetworkException(exception: Throwable) : Exception(exception.message, exception)
