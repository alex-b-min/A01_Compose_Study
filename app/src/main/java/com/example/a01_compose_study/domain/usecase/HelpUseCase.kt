package com.example.a01_compose_study.domain.usecase

import com.example.a01_compose_study.domain.repository.domain.HelpRepository

class HelpUseCase(
    private val repository: HelpRepository
) {
    operator fun invoke() {
        repository.action()
    }
}