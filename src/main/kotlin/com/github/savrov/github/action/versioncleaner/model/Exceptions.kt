package com.github.savrov.github.action.versioncleaner.model

data class ExceptionsBundle(private val exceptions: List<Throwable>) : Exception() {
    override val message: String
        get() = exceptions.joinToString("\n") { it.message ?: "" }
}

data class NetworkException(private val exception: Throwable) : Exception(exception.message, exception)
