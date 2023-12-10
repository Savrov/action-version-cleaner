package com.github.savrov.github.action.versioncleaner.model

data class Context(
    val owner: String,
    val repository: String,
    val packageType: String,
    val versionTag: String,
    val isVersionTagStrict: Boolean,
)
