package com.example.a01_compose_study.domain.repository

import com.example.a01_compose_study.domain.model.HelpItemData

interface HelpRepository {
    fun getHelpItems(): List<HelpItemData>
}