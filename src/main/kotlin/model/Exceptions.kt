package model

class ExceptionsBundle(private val throwables: List<Throwable>) : Exception() {
    override val message: String
        get() = throwables.joinToString("\n") { it.message ?: "" }
}