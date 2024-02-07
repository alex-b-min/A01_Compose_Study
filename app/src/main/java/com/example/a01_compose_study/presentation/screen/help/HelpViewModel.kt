package com.example.a01_compose_study.presentation.screen.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a01_compose_study.domain.model.ScreenType
import com.example.a01_compose_study.domain.usecase.HelpUseCase
import com.example.a01_compose_study.presentation.data.UiState
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.main.VRUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val helpUseCase: HelpUseCase,
) : ViewModel() {

    private val _domainUiState = UiState._domainUiState
    private val _domainWindowVisible = UiState._domainWindowVisible

    fun onHelpEvent(event: HelpEvent) {
        when (event) {
            is HelpEvent.HelpListItemOnClick -> {
                _domainUiState.update { domainUiState ->
                    (domainUiState as? DomainUiState.HelpWindow)?.copy(
                        screenType = ScreenType.HelpDetailList,
                        detailData = event.selectedHelpItem
                    ) ?: domainUiState
                }
            }

            is HelpEvent.OnHelpListBack -> {
                viewModelScope.launch {
                    _domainWindowVisible.value = false
                    delay(500)
                    _domainUiState.update { domainUiState ->
                        DomainUiState.NoneWindow()
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

            is HelpEvent.ChangeHelpWindowSizeEvent -> {
                /**
                 * 사이즈의 크기를 버튼을 통해 직접적으로 바꾸고 싶은 경우에 사용
                 */
                _domainUiState.update { uiState ->
                    uiState.copyWithNewSizeType(event.screenSizeType)
                }
            }

            is HelpEvent.OnDismiss -> {
                _domainWindowVisible.value = false
            }
        }
    }
}