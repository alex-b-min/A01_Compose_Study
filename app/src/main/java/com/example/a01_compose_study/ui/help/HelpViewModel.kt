package com.example.a01_compose_study.ui.help

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.model.HelpItemData
import com.example.a01_compose_study.domain.usecase.HelpUsecase
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.main.DomainUiState
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.domain.ScreenType
import com.example.a01_compose_study.domain.usecase.VRUseCase
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.main.DomainUiState
import com.example.a01_compose_study.presentation.main.VRUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val VRUsecase: VRUseCase,
) : ViewModel() {
    
    private val _domainUiState = UiState._domainUiState

    private val _vrUiState = UiState._vrUiState

    private val _domainWindowVisible = UiState._domainWindowVisible

    fun onHelpEvent(event: HelpEvent) {
        when (event) {
            is HelpEvent.OnDismiss -> {
                _domainWindowVisible.value = false
            }
            is HelpEvent.OnHelpListBack -> {
                viewModelScope.launch {
                    _domainWindowVisible.value = false
                    delay(500)
                    _domainUiState.update { domainUiState ->
                        DomainUiState.NoneWindow()
                    }

                    _vrUiState.update { vrUiState ->
                        VRUiState.VRWindow(
                            visible = true,
                            isError = event.isError,
                            text = event.text,
                            screenSizeType = event.screenSizeType
                        )
                    }
                }
            }
            is HelpEvent.OnHelpDetailBack -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.HelpWindow)?.copy(
                        screenType = ScreenType.HelpList,
                    ) ?: domainUiState
                }
            }
            is HelpEvent.SelectHelpItem -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.HelpWindow)?.copy(
                        screenType = ScreenType.HelpDetailList,
                        detailData = event.selectedHelpItem
                    ) ?: domainUiState
                }
            }
            is HelpEvent.ChangeHelpWindowSizeEvent -> {
                _domainUiState.update { uiState ->
                    uiState.copyWithNewSizeType(event.screenSizeType)
                }
            }
        }
    }
    

    fun getHelpList(): List<HelpItemData> {
        return helpUsecase.invoke()
    }


}