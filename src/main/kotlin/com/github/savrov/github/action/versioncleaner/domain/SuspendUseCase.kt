package com.github.savrov.github.action.versioncleaner.domain

internal interface SuspendUseCase<INPUT, OUTPUT> {
    suspend operator fun invoke(input: INPUT): OUTPUT
}
