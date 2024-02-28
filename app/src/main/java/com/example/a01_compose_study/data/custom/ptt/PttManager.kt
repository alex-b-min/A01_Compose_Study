package com.example.a01_compose_study.data.custom.ptt

import android.content.Context
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.analyze.ParseBundle
import com.example.a01_compose_study.data.custom.MWContext
import com.example.a01_compose_study.data.custom.VRResultListener
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PttManager @Inject constructor(
    val context: Context,
    val vrmwManager: VrmwManager
) : VRResultListener {

    override fun onReceiveBundle(bundle: ParseBundle<out Any>) {
        TODO("Not yet implemented")
    }

    override fun onBundleParsingErr() {
        TODO("Not yet implemented")
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onVRError(error: HVRError) {
        TODO("Not yet implemented")
    }

    fun pttEvent() {
        val mwContext = MWContext(DialogueMode.MAINMENU, this)

    }
}