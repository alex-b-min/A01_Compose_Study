package com.example.a01_compose_study.domain.usecase

import com.example.a01_compose_study.domain.model.VRResultAdapter
import com.example.a01_compose_study.domain.repository.vr.VRRepository

class VRUseCase(
    private val repository: VRRepository,
) {
    operator fun invoke(): VRResultAdapter {
        return repository.onVRResult()
    }
}