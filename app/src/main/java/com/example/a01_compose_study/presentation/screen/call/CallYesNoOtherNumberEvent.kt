package com.example.a01_compose_study.presentation.screen.call

/**
 * 음성인식 발화 데이터를 통해 클릭 이벤트를 발생시켜야 해서 상태를 만듦.
 */
sealed class CallYesNoOtherNumberEvent {

    object Yes : CallYesNoOtherNumberEvent()
    object No : CallYesNoOtherNumberEvent()
    object OtherNumber : CallYesNoOtherNumberEvent()
    object None : CallYesNoOtherNumberEvent()
}