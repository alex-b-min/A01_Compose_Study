package com.example.a01_compose_study.domain.usecase

import com.example.a01_compose_study.domain.repository.VRRepository

class VRUsecase(
    private val repository: VRRepository
) {
    operator fun invoke(): Any {
        return repository.onVRResult()
    }
}