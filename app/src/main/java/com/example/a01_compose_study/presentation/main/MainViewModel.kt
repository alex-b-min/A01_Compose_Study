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
                    MainUiState.HelpWindow(
                        visible = true,
                        text = event.text,
                        screenSizeType = event.screenSizeType
                    )
                }
            }
            // TODO(다른 시나리오 Window를 열었을 때 해당 Window를 띄우도록 해야함)
        }
    }

    fun closeHelpWindow() {
        _uiState.update {
            MainUiState.HelpWindow(
                visible = false
            )
        }
    }
}
