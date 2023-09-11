package model

data class Context(
    val organization: String,
    val repository: String,
    val packageType: String,
    val snapshotTag: String,
)
