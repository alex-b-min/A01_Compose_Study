package com.example.a01_compose_study.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.NoneWindow)
    val uiState: StateFlow<MainUiState> = _uiState

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.CloseWindowEvent -> {
                _uiState.update { uiState ->
                    MainUiState.NoneWindow
                }
            }

            is MainEvent.OpenHelpWindowEvent -> {
                _uiState.update { uiState ->
                    /**
                     * TODO 여기서 데이터와 관련된 비즈니스 로직을 실행하고 그 결과값(success/true)을 isError 인자에 넣어준다.
                     * 하지만 현재, 데이터 발행이 없기에 이벤트가 발생될 때 직접 생성하는 방식으로 성공과 에러를 발생시키도록 한다.
                     */
                    MainUiState.HelpWindow(
                        visible = true,
                        isError = event.isError,
                        text = event.text,
                        screenSizeType = event.screenSizeType
                    )
                }
            }
            // TODO(다른 시나리오 Window를 열었을 때 해당 Window를 띄우도록 해야함)
        }
    }

    fun closeHelpWindow() {
        // 현재의 error 상태에 따른 glow 애니메이션창을 내려야하기 때문에 isError에 uiState.isError로 설정
        if (_uiState.value is MainUiState.HelpWindow) {
            _uiState.update { uiState ->
                (uiState as? MainUiState.HelpWindow)?.copy(
                    visible = false,
                    isError = uiState.isError
                ) ?: uiState
            }
        }
    }
}
