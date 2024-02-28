package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.HVRError
import com.example.a01_compose_study.data.analyze.ParseBundle

interface VRResultListener {
    fun onReceiveBundle(bundle: ParseBundle<out Any>)
    fun onBundleParsingErr()
    fun onCancel()
    fun onVRError(error: HVRError)
}