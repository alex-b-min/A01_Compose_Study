package com.example.a01_compose_study.data.custom

import android.util.Log
import androidx.room.PrimaryKey
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.analyze.ParserFactory
import com.example.a01_compose_study.data.custom.call.CallManager
import com.example.a01_compose_study.data.custom.sendMsg.SendMsgManager
import com.example.a01_compose_study.presentation.data.ServiceState
import com.example.a01_compose_study.presentation.data.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataProducer @Inject constructor(
    val helpManager: HelpManager,
    val callManager: CallManager,
    val sendMsgManager: SendMsgManager,
) {
}