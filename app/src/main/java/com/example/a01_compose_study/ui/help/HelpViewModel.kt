package com.example.a01_compose_study.ui.help

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.domain.usecase.HelpUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val helpUsecase: HelpUsecase
) : ViewModel() {

}