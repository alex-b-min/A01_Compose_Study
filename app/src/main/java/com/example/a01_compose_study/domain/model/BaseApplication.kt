package com.example.a01_compose_study.domain.model

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.a01_compose_study.domain.util.CustomLogger
import java.util.Locale

open class BaseApplication : Application() {

    val TAG = this.javaClass.simpleName

    lateinit var mViewModelStore: ViewModelStore
    lateinit var viewModelStoreOwner: ViewModelStoreOwner

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
    }

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        CustomLogger.i("onCreate ${TAG}")

        appContext = applicationContext

        //serviceViewModel.printLocaleInfo()
        //updateLanguage(this)
        //serviceViewModel.printLocaleInfo()

        mViewModelStore = ViewModelStore()
        viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = mViewModelStore

        }
        //startMain()
    }

    override fun onTerminate() {
        super.onTerminate()
        CustomLogger.i("onTerminate")
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        var ret = false
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (manager != null) {
            val runningServices = manager.getRunningServices(Int.MAX_VALUE)
            if (runningServices != null) {
                for (service in runningServices) {
                    if (serviceClass.name == service.service.className) {
                        ret = true
                        break
                    }
                }
            }
        }
        CustomLogger.i("isServiceRunning : ${ret}")
        return ret
    }

//    fun startMain() {
//        if (!isServiceRunning(AwindowService::class.java)) {
//            CustomLogger.i("startForegroundService")
//            startForegroundService(Intent(this, AwindowService::class.java))
//        } else {
//            CustomLogger.i("send RESUME_SERVICE")
//            val intent = Intent(this, AwindowService::class.java)
//            intent.action = "RESUME_SERVICE"
//            startService(intent)
//        }
//    }

    fun updateLanguage(ctx: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val lang = prefs.getString("locale_override", "en")
        updateLanguage(ctx, lang)
    }

    fun updateLanguage(ctx: Context, lang: String?) {
        val cfg = Configuration()
        if (!TextUtils.isEmpty(lang)) cfg.locale = Locale(lang) else cfg.locale =
            Locale.getDefault()
        ctx.resources.updateConfiguration(cfg, null)
    }
}
