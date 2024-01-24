package com.example.a01_compose_study.data

import com.example.a01_compose_study.domain.state.ScreenState
import com.example.a01_compose_study.domain.model.BaseModel


import android.graphics.Bitmap
import com.example.a01_compose_study.domain.model.BaseManager
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

open class ScreenData(val domainType: DomainType, val screenType: ScreenType) {
    var mwContext: MWContext? = null
    var screenState = ScreenState()
    var onStart: Runnable? = null
    var onResume: Runnable? = null
    var onPop: Runnable? = null
    var manager: BaseManager? = null
    var model: BaseModel? = null
    var tag: String = ""
    var bitmap: Bitmap? = null
    var skipBitmap: Boolean = false
    var windowMode: WindowMode? = null

    open fun onStart() {
        onStart?.run()
    }

    open fun onResume() {
        onResume?.run()
    }

    open fun onPop() {
        onPop?.run()
        release()
    }

    fun isBitmapReady(): Boolean {
        bitmap?.let {
            if (!it.isRecycled) {
                return true
            }
        }
        return false
    }

    fun release() {
        CoroutineScope(Dispatchers.Main).launch {
            CoroutineScope(Dispatchers.Default).launch {
                bitmap?.let {
                    if (!it.isRecycled) {
                        it.recycle()
                        CustomLogger.e("screen tag:${tag} [${domainType}] [${screenType}] Bitmap Released")
                    }
                }
                bitmap = null
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }

    override fun toString(): String {
        return "HashCode[${this.hashCode()}], mwContext[$mwContext], domainType[$domainType], screenType[$screenType]"
    }
}

