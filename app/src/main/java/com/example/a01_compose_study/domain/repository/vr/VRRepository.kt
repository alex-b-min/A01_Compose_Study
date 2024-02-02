package com.example.a01_compose_study.domain.repository.vr

import com.example.a01_compose_study.domain.model.VRResultAdapter

interface VRRepository {
    fun onVRResult(): VRResultAdapter
}