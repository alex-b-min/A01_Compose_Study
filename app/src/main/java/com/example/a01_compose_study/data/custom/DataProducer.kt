package com.example.a01_compose_study.data.custom

import android.util.Log
import androidx.room.PrimaryKey
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.VRResult
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.analyze.ParserFactory
import com.example.a01_compose_study.data.custom.call.CallManager
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
    val callManager: CallManager
) {

    init {
        managerInit()
    }

    private fun managerInit() {
        ServiceState.mwContext.dataProducer = this
    }

    /**
     * 파싱된 데이터를 하나로 묶기 위한 StateFlow 타입의 sealedParsedData 전역변수
     */
    private val _sealedParsedData = UiState._sealedParsedData
    val sealedParsedData: SharedFlow<SealedParsedData> = UiState._sealedParsedData

    private val job = CoroutineScope(Dispatchers.Default)

    fun onVRResult(vrResult: VRResult) {
        val bundle = ParserFactory().dataParsing(vrResult, dialogueMode = DialogueMode.HELP)
//        bundle?.type = ParseDomainType.HELP
        bundle?.type = ParseDomainType.CALL

        when (bundle?.type) {
            ParseDomainType.HELP -> {
                /**
                 * 기존에는 적절한 각 DomainManager의 onReceiveBundle()에 해당 bundle의 값을 넣어주는 방식
                 * ==> 바뀐 방식은 아래와 같음
                 * 각 DoaminManager에서 bundle의 값을 넣으면 파싱하여 적절한 데이터을 리턴해주는 함수를 구현한다.
                 * 참고로 Help의 경우,
                 * domainType/screenType/screenSizeType/data 의 데이터를 가져야하므로 ProcHelpData라는 데이터 클래스를 한 개 생성함
                 */
                val procHelpData = helpManager.parsedData(bundle)
                job.launch {
                    _sealedParsedData.emit(SealedParsedData.HelpData(procHelpData))
                }
            }
            ParseDomainType.CALL -> {
                val procHelpData = helpManager.parsedData(bundle)
                job.launch {
                    _sealedParsedData.emit(SealedParsedData.HelpData(procHelpData))
                }
            }

            else -> {
                /**
                 * 다른 도메인 타입들에 대한 값들이 들어간다.
                 */
            }
        }
    }
}