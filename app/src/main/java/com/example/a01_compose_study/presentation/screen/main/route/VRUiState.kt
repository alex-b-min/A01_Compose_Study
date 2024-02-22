package com.example.a01_compose_study.presentation.screen.main.route

sealed class VRUiState(
    val active: Boolean = false,
    val isError: Boolean = false
) {

    class PttNone(
        active: Boolean,
        isError: Boolean
    ) : VRUiState(active, isError)

    class PttListen(
        active: Boolean,
        isError: Boolean
    ) : VRUiState(active, isError)

    class PttLoading(
        active: Boolean,
        isError: Boolean
    ) : VRUiState(active, isError)

    class PttSpeak(
        active: Boolean,
        isError: Boolean
    ) : VRUiState(active, isError)
}