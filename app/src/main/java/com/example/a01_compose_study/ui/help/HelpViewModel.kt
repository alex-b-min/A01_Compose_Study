package com.example.a01_compose_study.ui.help

import android.content.Context
import com.example.a01_compose_study.data.DialogueMode
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.ScreenData
import com.example.a01_compose_study.data.ScreenType
import com.example.a01_compose_study.data.VrConfig
import com.example.a01_compose_study.data.WindowMode
import com.example.a01_compose_study.data.repositoryImpl.JobManager
import com.example.a01_compose_study.domain.state.NetworkState
import com.example.a01_compose_study.domain.model.BaseManager
import com.example.a01_compose_study.domain.model.HelpCommand
import com.example.a01_compose_study.domain.model.HelpModel
import com.example.a01_compose_study.domain.model.ParseBundle
import com.example.a01_compose_study.domain.model.ServiceViewModel
import com.example.a01_compose_study.domain.model.VrmwManager
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.shared.SharedProperty
import com.example.a01_compose_study.presentation.shared.WindowService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HelpViewModel @Inject constructor(
    @ApplicationContext context: Context,
    vrmwManager: VrmwManager,
    viewModel: ServiceViewModel,
    val helpCommand: HelpCommand,
) : BaseManager(context, viewModel , vrmwManager) {
    override val TAG = this.javaClass.simpleName

    val windowService = WindowService
    val sharedProperty = SharedProperty

    private var _detailIndex = 0
    val detailIndex = _detailIndex

    // NetworkHeader 상태관리
    val networkState = NetworkState()

    val vrConfig = MutableStateFlow(VrConfig())

    private var helpModelOrg: HelpModel? = HelpModel("")


    init {
        CustomLogger.i("HelpManager Constructor Hash[${this.hashCode()}] [${context.hashCode()}] [${viewModel.hashCode()}] [${vrmwManager.hashCode()}]")
    }

    override fun init() {

    }

    fun onEvent(event: HelpEvent) {
        when (event) {
            is HelpEvent.CancelHelp -> {
                windowService.hideWindow()
            }

            // Help Detail view에서 back 버튼 -> Help view
            is HelpEvent.SetHelpListView -> {
                setHelpListView(ScreenType.HelpList)
            }

            is HelpEvent.SelectIndex -> {
                CustomLogger.d("onSelectIndex serviceIndex $event.serviceIndex")
                setHelpDetailListView(event.serviceIndex)
            }

            is HelpEvent.ReceiveBundle -> {
                super.onReceiveBundle(event.bundle)
                if (event.bundle.isBundleConsumed) {
                    CustomLogger.i("bundleConsumed by BaseManager")
                    return
                }
                addJob(sharedProperty, event.bundle)
            }

            else -> {}
        }

    }


    private fun getListRunnable(
        helpModel: HelpModel,
    ): Runnable {
        return kotlinx.coroutines.Runnable {
            CustomLogger.i("getListRunnable helpList guidanceList size : ${helpModel.domainIdList.size}")
            val promptId = listOf("PID_CMN_COMM_01_11")
            val screenData = ScreenData(DomainType.Help, ScreenType.HelpList).apply {
                skipBitmap = true
                onStart = kotlinx.coroutines.Runnable {
                    windowService.changeWindowMode(WindowMode.LARGE)
                    vrmwManager.requestTTS(promptId)
                }
                onResume = kotlinx.coroutines.Runnable {
                    windowService.changeWindowMode(WindowMode.LARGE)
                    vrmwManager.requestTTS(promptId)
                }
                manager = this@HelpViewModel
                model = helpModel
            }
            windowService.addScreen(screenData)

        }
    }

    private fun procHelpIntention(bundle: ParseBundle<out Any>) {
        val helpModel = bundle.model as? HelpModel

        helpModel?.apply {
            domainIdList = helpCommand.makeHelpCommand(
                vrConfig.value.isSupportServer,
                getSettingState().isOnlineVR(),
                networkState.vehicleName
            )
        }
        helpModelOrg = helpModel

        helpModel?.let {
            if (helpModel.domainIdList.size > 0) {
                getListRunnable(helpModel).run()
            }
        } ?: run {
            reject()
        }
    }

    private fun addJob(jobManager: JobManager, bundle: ParseBundle<out Any>) {
        jobManager.addJob("onReceiveBundle") {
            when (bundle.dialogueMode) {
                DialogueMode.HELP -> {
                    val helpModel = bundle.model as? HelpModel
                    helpModel?.let {
                        procHelpIntention(bundle)
                    } ?: run {
                        reject()
                    }
                }

                else -> {

                }
            }
        }
    }


    override fun onBundleParsingErr() {
    }

    private fun setHelpDetailListView(index: Int) {
        _detailIndex = index
        setHelpListView(ScreenType.HelpDetailList)
    }

    private fun setHelpListView(screenType: ScreenType) {
        val screenData = ScreenData(DomainType.Help, screenType)
        screenData.skipBitmap = true
        screenData.manager = this@HelpViewModel
        screenData.onStart = kotlinx.coroutines.Runnable {
            windowService.changeWindowMode(WindowMode.LARGE)
        }
        screenData.model = helpModelOrg
        windowService.addScreen(screenData)
    }
}