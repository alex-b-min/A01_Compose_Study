package com.example.a01_compose_study.domain.usecase

import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.repository.HelpRepository

class HelpUsecase(
    private val repository: HelpRepository
) {
    operator fun invoke(): List<HelpItemData> {
        return repository.getHelpItems()
    }
}