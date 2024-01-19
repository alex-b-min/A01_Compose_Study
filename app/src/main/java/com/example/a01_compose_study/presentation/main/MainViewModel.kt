package com.example.a01_compose_study.presentation.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.DoneScreen)
    val uiState: StateFlow<MainUiState> = _uiState

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.Close -> {
                _uiState.update { uiState ->
                    MainUiState.DoneScreen
                }
            }

            is MainEvent.OneScreenOpen -> {
                _uiState.update { uiState ->
                    MainUiState.OneScreen(
                        isVisible = true,
                        text = event.text
                    )
                }
            }

            is MainEvent.TwoScreenOpen -> {
                _uiState.update { uiState ->
                    MainUiState.TwoScreen(
                        isVisible = true,
                        text = event.text,
                        screenSizeType = event.screenSizeType
                    )
                }
            }
        }
    }
}