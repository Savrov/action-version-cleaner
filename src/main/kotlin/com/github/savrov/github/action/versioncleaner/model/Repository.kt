package com.github.savrov.github.action.versioncleaner.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    @SerialName("name") val name: String,
    @SerialName("owner") val owner: Owner,
)
