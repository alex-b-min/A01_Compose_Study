package com.example.a01_compose_study.data.custom.ptt

import android.content.Context
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.analyze.ParseDomainType
import com.example.a01_compose_study.data.custom.DataProducer
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PttManager @Inject constructor(
    val context: Context,
    val vrmwManager: VrmwManager,
    val dataProducer: DataProducer
) : VRResultListener {

    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {


        when(bundle.type) {
            ParseDomainType.CALL -> {
                dataProducer.callManager.onReceiveBundle(bundle)
            }

            ParseDomainType.NAVI -> {
                TODO("NAVI 매니저 추가")
            }

            ParseDomainType.RADIO -> {
                TODO("RADIO 매니저 추가")
            }

            ParseDomainType.WEATHER -> {
                TODO("WEATHER 매니저 추가")
            }

            ParseDomainType.SEND_MESSAGE -> {
                TODO("SEND_MESSAGE 매니저 추가")
            }

            ParseDomainType.HELP -> {
                dataProducer.helpManager.onReceiveBundle(bundle)
            }

            else -> {

            }
        }
    }

    override fun onBundleParsingErr() {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    override fun onCancel() {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    override fun onVRError(error: HVRError) {
        TODO("BaseManager 클래스를 만들어 공통적으로 처리")
    }

    fun pttEvent() {
        val mwContext = MWContext(DialogueMode.MAINMENU, this)

        vrmwManager.startVR(mwContext)
    }
}