package com.example.a01_compose_study.data.custom

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.example.a01_compose_study.R
import com.example.a01_compose_study.data.ACLDevice
import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.data.DomainType
import com.example.a01_compose_study.data.HLanguageType
import com.example.a01_compose_study.data.HVRConfigEvent
import com.example.a01_compose_study.data.HVRG2PMode
import com.example.a01_compose_study.data.HfpDevice
import com.example.a01_compose_study.data.PhonebookDownloadState
import com.example.a01_compose_study.data.custom.NormalizeUtils.convertTextNormalize
import com.example.a01_compose_study.di.ApplicationScope
import com.example.a01_compose_study.di.IoDispatcher
import com.example.a01_compose_study.domain.model.BaseManager
import com.example.a01_compose_study.domain.model.NoticeModel
import com.example.a01_compose_study.domain.state.MWState
import com.example.a01_compose_study.domain.util.CustomLogger
import com.example.a01_compose_study.presentation.data.ServiceState.contactDownloadState
import com.example.a01_compose_study.presentation.data.ServiceState.g2pCompleteCnt
import com.example.a01_compose_study.presentation.data.ServiceState.isDevelopMode
import com.example.a01_compose_study.presentation.data.ServiceState.isWaitingPBG2PState
import com.example.a01_compose_study.presentation.data.ServiceState.languageType
import com.example.a01_compose_study.presentation.data.ServiceState.mwState
import com.example.a01_compose_study.presentation.data.ServiceState.settingState
import com.example.a01_compose_study.presentation.data.ServiceState.systemState
import com.example.a01_compose_study.presentation.data.ServiceState.vrConfig
import com.example.a01_compose_study.presentation.data.UiState.changeUiState
import com.example.a01_compose_study.presentation.data.UiState.mwContext
import com.example.a01_compose_study.presentation.screen.main.DomainUiState
import com.example.a01_compose_study.presentation.screen.ptt.VrmwManager
import com.example.a01_compose_study.presentation.util.StringUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsManager @Inject constructor(
    @ApplicationContext context: Context,
    vrmwManager: VrmwManager,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val contactsRepository: ContactsRepository,
) : BaseManager(context, vrmwManager) {

    var fetchJob: Job? = null
    var clearJob: Job? = null
    val fetchTrackerList = MutableStateFlow<Job?>(null)

    private val _contactsList = MutableStateFlow(mutableListOf<Contact>())
    val contactsList = _contactsList.asStateFlow()

//    private val _contactsCallLogList = MutableStateFlow(listOf<ContactCallLog>())
//    val contactsCallLogList = _contactsCallLogList.asStateFlow()

    init {
        CustomLogger.i("ContactsManager Constructor Hash[${this.hashCode()}]")
    }

    /**
     * 초기화
     */
    override fun init() {

        CoroutineScope(Dispatchers.Default).launch {
            fetchPhoneBookState()
        }
        setObserve()
    }

//    override fun onBundleParsingErr() {
//
//    }

    private fun setObserve() {
        coroutineScope.launch {
            contactsList.collectLatest { contactsList ->
                CustomLogger.e("[Contact][fetchContacts size: ${contactsList.size}, address: ${getBluetoothState().hfpDevice.value.device}, connect: ${getBluetoothState().hfpDevice.value.connect}, hfpConnected:${getBluetoothState().hfpDevice.value}")
            }
        }

        coroutineScope.launch {
            /**
             *  hfp 상태를 관찰하여 업데이트
             */
            getBluetoothState().hfpDevice.collectLatest { hfp ->
                val address = hfp.device
                val connect = hfp.connect
                val isRecognizing = hfp.recognizing

                CustomLogger.e("[Contact][hfpDevice address: $address, connect:$connect, isRecognizing:$isRecognizing, size:${contactsList.value.size}")

                if (!hfp.recognizing) {
                    vrmwManager.g2pController.updateCacheFiles(address)
                }
            }
        }

        coroutineScope.launch {
            getMwState().vrConfigEvent.collect {
                CustomLogger.i("vrConfigEvent: $it")
                if (it == HVRConfigEvent.INITIALIZE_DONE && languageType.value != HLanguageType.MAX) {
                    removeCachefiles("FF:FF:viewModel.bluetoothStateFF:FF:FF:FF")
                    fetchContacts()
                }
            }
        }

        coroutineScope.launch {
            settingState.callState.collect {
                CustomLogger.d("call state : ${settingState.callState.value}")
                if (!vrmwManager.isIdle()) {
                    when (it) {
                        // ringing (Incoming call)
                        "1" -> {
                            // Incoming call -> End VR
                            // Incoming call (AA/CP) -> End VR
                            CustomLogger.e("hideWindow by ringing (Incoming call)")
//                            viewModel.hideWindow()
                            TODO("hideWindow")
                        }
                        // offhook (Outgoing call, calling)
                        "2" -> {
                            CustomLogger.e("hideWindow by Outgoing call, calling")
                            // End VR and start Bluetooth action
//                            viewModel.hideWindow()
                            TODO("hideWindow")
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("LogNotTimber", "PrivateApi")
    fun getSystemProperty(key: String): String {
        var property = StringUtils.EMPTY
        try {
            CustomLogger.e("getSystemProperty() key:$key")

            val systemProperties = Class.forName("android.os.SystemProperties")
            val paramTypes = arrayOf<Class<*>>(String::class.java)
            val get = systemProperties.getMethod("get", *paramTypes)
            val params = arrayOf<Any>(key)
            property = get.invoke(systemProperties, *params) as String

            CustomLogger.e("getSystemProperty() property:$property")
        } catch (e: IllegalArgumentException) {
            CustomLogger.e("getSystemProperty() $e")
        } catch (e: Exception) {
            CustomLogger.e("getSystemProperty() $e")
        }
        return property
    }

    @SuppressLint("LogNotTimber", "PrivateApi")
    fun setSystemProperty(key: String, value: String) {
        try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val paramTypes = arrayOf<Class<*>>(String::class.java, String::class.java)
            val set = systemProperties.getMethod("set", *paramTypes)
            val params = arrayOf<Any>(key, value)
            set.invoke(systemProperties, *params)
            CustomLogger.d("setSystemProperty() success!")
        } catch (e: IllegalArgumentException) {
            CustomLogger.e("setSystemProperty() $e")
        } catch (e: Exception) {
            CustomLogger.e("setSystemProperty() $e")
        }
    }

    private fun appleCarplayConditionCheck(): NoticeModel? {
        CustomLogger.i("appleCarplayConditionCheck")
        if (systemState.isCPConnected.value) {
            return NoticeModel().apply {
                noticeString = context.getString(R.string.TID_SCH_PHON_02_01)
                noticePromptId = "PID_SCH_PHON_02_01"
            }
        }
        return null
    }


    private fun bluetoothPhoneConnectionCheck(domainType: DomainType): NoticeModel? {
        CustomLogger.i("bluetoothPhoneConnectionCheck")

//        if (viewModel.isDevelopMode.value) {
//            return null
//        }

        // check Bluetooth on Progress (Pairing, Connecting)
        CustomLogger.i("bluetoothPhoneConnectionCheck Bluetooth on Progress - hfpConnectState: ${getBluetoothState().hfpConnectState.value}")
        if ((getBluetoothState().hfpConnectState.value == BluetoothProfile.STATE_CONNECTING) || (getBluetoothState().bluetoothBondState.value == BluetoothDevice.BOND_BONDING)) {
            return NoticeModel().apply {
                noticeString = context.getString(R.string.TID_SCH_PHON_03_01)
                noticePromptId = "PID_SCH_PHON_03_01"
                args["changeScreen"] = 0
                screenExist = true
            }
        }

        // check Bluetooth Connected
        CustomLogger.i("bluetoothPhoneConnectionCheck Bluetooth Connected")
        if (getBluetoothState().hfpConnectState.value == BluetoothProfile.STATE_CONNECTED) {
            return null
        } else {
            CustomLogger.i("bluetoothPhoneConnectionCheck Any paired device : ${settingState.btPairedDeviceSize.value}")
            // check Any paired device
            if (settingState.btPairedDeviceSize.value > 0) {
                when (domainType) {
                    DomainType.Call -> {
                        return NoticeModel().apply {
                            noticeString = context.getString(R.string.TID_SCH_PHON_03_02)
                            noticePromptId = "PID_SCH_PHON_03_02"
                            args["changeScreen"] = 0
                            screenExist = true
                        }
                    }

                    DomainType.SendMessage -> {
                        return NoticeModel().apply {
                            noticeString = context.getString(R.string.TID_SCH_PHON_03_07)
                            noticePromptId = "PID_SCH_PHON_03_07"
                            args["changeScreen"] = 0
                            screenExist = true
                        }
                    }

                    else -> {
                        return null
                    }
                }
            } else {
                return NoticeModel().apply {
                    noticeString = context.getString(R.string.TID_SCH_PHON_03_03)
                    noticePromptId = "PID_SCH_PHON_03_03"
                    args["changeScreen"] = 0
                    screenExist = true
                }
            }
        }
    }

    private fun mapConnectedCheck(domainType: DomainType): NoticeModel? {
        CustomLogger.i("mapConnectedCheck")

        // check Bluetooth phone Connected? (HFP)
        CustomLogger.i("bluetoothPhoneConnectionCheck Bluetooth Connected?")
        if (getBluetoothState().hfpConnectState.value == BluetoothProfile.STATE_CONNECTED) {
            // Map Support? 연결된 기기에서 메시지 전송 가능?

            // Map Connected
            if (getBluetoothState().mapConnectState.value == BluetoothProfile.STATE_CONNECTED) {
                return null
            } else {
                return NoticeModel().apply {
                    noticeString = context.getString(R.string.TID_SCH_PHON_14_01)
                    noticePromptId = "PID_SCH_PHON_14_01"
                    args["changeScreen"] = 3
                    screenExist = true
                }
            }
        } else {
            CustomLogger.i("Bluetooth Phone Connection Check")
            // check Any paired device
            return bluetoothPhoneConnectionCheck(domainType)
        }
    }

    private fun contactListCheck(domainType: DomainType): NoticeModel? {
        CustomLogger.i("contactListCheck")
        // check System has contact list?
        CustomLogger.i("contactListCheck contactsList.value.size : ${contactsList.value.size}")
        if (contactsList.value.size != 0) {
            // check Voice Data Check
            return voiceDataCheck()
        } else {
            // Requesting Download (확인 필요) / Downloading in prograss?
            return if (contactDownloadState.value != PhonebookDownloadState.ACTION_PULL_COMPLETE) {
                // Requesting Download -> request popup 띄우기 필요..
                // Downloading in prograss
                // START 판단 불가로 COMPLETE 조건 변경 (05_02 표시)
                return NoticeModel().apply {
                    noticeString = context.getString(R.string.TID_SCH_PHON_05_02)
                    noticePromptId = "PID_SCH_PHON_05_02"
                    args["changeScreen"] = 1
                    screenExist = true
                }
            } else {
                // check contact list Download check
                contactListDownloadCheck(domainType)
            }
        }
    }

    private fun voiceDataCheck(): NoticeModel? {
        CustomLogger.i("voiceDataCheck")
        // Contact list data producing completed?
        val phoneG2PCnt = g2pCompleteCnt[HVRG2PMode.PHONE_BOOK]?.value ?: -1
        CustomLogger.i("voiceDataCheck isG2PCompleted : $phoneG2PCnt")

        // 초기값 -1, -1이 아니면 success
        if (phoneG2PCnt != -1) {
            // Recognizable Name find?
            if (phoneG2PCnt > 0) true else {
                return NoticeModel().apply {
                    noticeString = context.getString(R.string.TID_SCH_PHON_06_01)
                    noticePromptId = "PID_SCH_PHON_06_01"
                    args["changeScreen"] = 0
                    screenExist = true
                }
            }
        } else {
            return NoticeModel().apply {
                noticeString = context.getString(R.string.TID_SCH_PHON_05_01)
                noticePromptId = "PID_SCH_PHON_05_01"
            }
        }
        return null
    }

    private fun contactListDownloadCheck(domainType: DomainType): NoticeModel {
        CustomLogger.i("contactListDownloadCheck")
//        CustomLogger.i("contactListDownloadCheck pbapConnectState : ${viewModel.pbapConnectState.value}")
        // Download Possible? -> 동기화 여부가 아닌 폰북 연결 사용한지를 체크한다.
        // Android, IOS가 폰북 연결 상태가 다르다. -> 조건 삭제
//        if (viewModel.pbapConnectState.value == BluetoothProfile.STATE_CONNECTED) {
//            viewModel.conString.value = context.getString(R.string.TID_SCH_PHON_05_02)
//            viewModel.conPttString.value =
//                "No Contacts. Please download contacts and try again."
//            // Exit, Phone Contact downloading Screen
//            viewModel.conChangeScreen.value = 0
//            viewModel.conChangeScreenExist.value = true
//        } else {
        // Which flow? Send Message or Call
        if (domainType == DomainType.Call) {
            // Call 인 경우 Dial Number 동작으로 연결 -> A01은 지원 X, SW는 지원-> 내수 Text 동일하게 표시
            return NoticeModel().apply {
                noticeString = context.getString(R.string.TID_SCH_PHON_05_02)
                noticePromptId = "PID_SCH_PHON_05_02"
                args["changeScreen"] = 1
                screenExist = true
            }
        } else { // send Message
            return NoticeModel().apply {
                noticeString = context.getString(R.string.TID_SCH_PHON_05_02)
                noticePromptId = "PID_SCH_PHON_05_02"
                args["changeScreen"] = 1
                screenExist = true
            }
        }
//        }

    }

    fun preConditionCheck(domainType: DomainType): NoticeModel? {

        when (domainType) {
            DomainType.Call -> {

                // check Apple Carplay Condition Check
                appleCarplayConditionCheck()?.let {
                    return it
                }

                // check Bluetooth Phone Connection Check
                bluetoothPhoneConnectionCheck(domainType)?.let {
                    return it
                }

                // check Contact List Check (Voice Data, Contact List Download)
                contactListCheck(domainType)?.let {
                    return it
                }

//        // 'Call' 시나리오에서 "전화번호부 정보를 불러오는 중입니다" 띄운 후 화면 표시가 있다.
//        viewModel.conString.value = context.getString(R.string.TID_SCH_PHON_05_01)
//        viewModel.conPttString.value = "Loading Contacts List... Please wait."
//        viewModel.conChangeScreenExist.value = true
            }

            DomainType.SendMessage -> {
                // check Apple Carplay Condition Check
                appleCarplayConditionCheck()?.let {
                    return it
                }

                // TODO 추후 CCS 서버 동작이 정상화 될 때 활성화
                // CCS Subscribed?
//                ccsSubscribedCheck()?.let {
//                    return it
//                }
                // Server Response Check
//                onlineVRCheck()?.let {
//                    return it
//                }

                // check MAP Connected Check
                mapConnectedCheck(domainType)?.let {
                    return it
                }

                // check Contact List Check (Voice Data, Contact List Download)
                contactListCheck(domainType)?.let {
                    return it
                }
            }

            else -> {}
        }

        return null
    }

    /**
     * Fetch phone book state
     * VR 실행 시 시스템 이벤트 누락을 보완하기 위해서 최초 1번만 fetch 하도록 함.
     */
    private suspend fun fetchPhoneBookState() {
        CustomLogger.e("fetchPhoneBookState start")
        val phoneBookState = contactsRepository.fetchPhoneBookState()
        val contactDownloaded = phoneBookState.contactDownloaded
        val hfpAddress = phoneBookState.hfpDevice ?: ""
        val mapAddress = phoneBookState.mapDevice ?: ""
        val hfpConnect =
            if (!phoneBookState.hfpDevice.isNullOrEmpty()) PROFILE_CONNECTED else PROFILE_DISCONNECTED
        val mapConnect =
            if (!phoneBookState.mapDevice.isNullOrEmpty()) PROFILE_CONNECTED else PROFILE_DISCONNECTED
        CustomLogger.e("[fetchPhoneBookState successful] hfpAddress:$hfpAddress, mapAddress:$mapAddress, hfpConnect:$hfpConnect, mapConnect:$mapConnect, contactDownloaded:$contactDownloaded")

        // hfp direct
        getBluetoothState().hfpConnectState.value = hfpConnect
        getBluetoothState().hfpDevice.value = HfpDevice(
            if (hfpConnect == PROFILE_CONNECTED) hfpAddress else "",
            hfpConnect == PROFILE_CONNECTED,
            getBluetoothState().hfpDevice.value.recognizing
        )

        // map direct
        getBluetoothState().mapConnectState.value = mapConnect
        getBluetoothState().mapConnected.value = if (mapConnect == PROFILE_CONNECTED)
            Pair(mapAddress, true)
        else
            Pair("", false)

        CustomLogger.e("fetchPhoneBookState end. Hfp, Map Update Done")
    }

//    /**
//     * Fetch contacts call log
//     * 주소록 fetch
//     * BT Pairing 이 될때마다 id 가 바뀌므로 업데이트 해야 함.
//     */
//    fun fetchContactsCallLog() {
//        CustomLogger.d("[Contact]fetchContactsCallLog start")
//        coroutineScope.launch(ioDispatcher) {
//            CustomLogger.d("[Contact]fetchContactsCallLog")
//            val contactsCallLogList = contactsRepository.fetchContactsCallLog()
//            CustomLogger.d("[Contact][fetchContactsCallLog successful] contactsCallLogList size:${contactsCallLogList.size}, contactsList:$contactsCallLogList")
//            _contactsCallLogList.emit(contactsCallLogList)
//        }.apply {
//            invokeOnCompletion { throwable ->
//                when (throwable) {
//                    is CancellationException -> CustomLogger.d("[Contact]Job[fetchContactsCallLog] $throwable")
//                    else -> {
//                        CustomLogger.d("[Contact][fetchContactsCallLog successful] _contactsCallLogList size:${_contactsCallLogList.value.size}")
//                        CustomLogger.d("[Contact]Job[fetchContactsCallLog] is completed without no error")
//                    }
//                }
//            }
//        }
//    }

    /**
     * Remove Cache files
     * G2P 요청 전 Cache files 삭제
     */
    fun removeCachefiles(deviceId: String) {
        CustomLogger.d("removeCachefiles start")
        val g2pMode = HVRG2PMode.PHONE_BOOK

        if (deviceId.isNotEmpty()) {
            vrmwManager.g2pController.removeCacheFiles(g2pMode.ordinal, deviceId)
        }
    }

    /**
     * Check Wating PB G2P state and Cancel G2P
     * 폰북 G2P 요청한 후 기다리는 상태인지 확인
     * 기다리는 상태라면 g2p cancel 요청
     */
    fun checkWaitingPBG2PStateCancel() {
        CustomLogger.d("checkWaitingPBG2PStateCancel")
        if (isWaitingPBG2PState.value) {
            vrmwManager.g2pController.cancel(HVRG2PMode.PHONE_BOOK)
        }
    }

    /**
     * Fetch contacts
     * 주소록 fetch
     * BT Pairing 이 될때마다 id 가 바뀌므로 업데이트 해야 함.
     */
    fun fetchContacts() {
        fetchJob?.takeIf { it.isActive }?.apply {
            cancel(
                "Job[fetchContacts] is cancelled by Manager",
                InterruptedException("Cancelled Forcibly")
            )
        }
        fetchJob = coroutineScope.launch(ioDispatcher) {
            CustomLogger.d("[Contact]fetchContacts start")

            // ASR 지원하지 않는 경우 G2P 요청 X -> phonebook 가져올 필요 X
            if (!vrConfig.value.isSupportASR) {
                CustomLogger.i("It's not Support ASR")
            } else {
                // BT 연결 체크
                if (getBluetoothState().hfpDevice.value.device.isNotEmpty() && getBluetoothState().hfpDevice.value.connect) {
                    // contact BT Phone App으로부터 받아옴
                    var contactsList = contactsRepository.fetchContacts()
                    CustomLogger.d("[Contact][fetchContacts successful] contactsList size:${contactsList.size}")

                    if (contactsList.isEmpty()) {
                        //다운받은 실제 주소록에 전번이 없는 경우 StateFlow 변화 상태가 아니라서 직접 업로드 함
                        _contactsList.emit(mutableListOf())

                    } else {
                        //주소록이 있다면 StateFlow 에 전달하여 upload 하도록 함
                        CustomLogger.d("[Contact][fetchContacts emit] contactsList size:${contactsList.size}")
                        _contactsList.emit(contactsList.toMutableList())
                    }
                } else {
                    CustomLogger.i("It's not BT Connect")
                }
            }

            if (isDevelopMode.value) {
                val developeContactList: List<Contact> = mutableListOf()
                val result = developeContactList.toMutableList()
                result.add(
                    Contact(
                        id = "880",
                        contact_id = "880",
                        name = "Jordan",
                        number = "010-1111-1111",
                        type = 1
                    )
                )

                result.add(
                    Contact(
                        id = "8801",
                        contact_id = "880",
                        name = "Jordan",
                        number = "010-1111-2222",
                        type = 2
                    )
                )
//                result.add(
//                    Contact(
//                        id = "8802",
//                        contact_id = "880",
//                        name = "Jordan",
//                        number = "010-1111-2223",
//                        type = 2
//                    )
//                )

//                result.add(
//                    Contact(
//                        id = "8803",
//                        contact_id = "880",
//                        name = "Jordan",
//                        number = "010-1111-2224",
//                        type = 2
//                    )
//                )
//                result.add(
//                    Contact(
//                        id = "7801",
//                        contact_id = "780",
//                        name = "Jordan",
//                        number = "010-1111-2222",
//                        type = 3
//                    )
//                )
//
//                result.add(
//                    Contact(
//                        id = "7802",
//                        contact_id = "780",
//                        name = "Jordan",
//                        number = "010-2222-2222",
//                        type = 4
//                    )
//                )
//                result.add(
//                    Contact(
//                        id = "7803",
//                        contact_id = "780",
//                        name = "Jordan",
//                        number = "010-2222-3333",
//                        type = 2
//                    )
//                )
                result.add(
                    Contact(
                        id = "881",
                        contact_id = "881",
                        name = "Test One",
                        number = "010-1111-2222"
                    )
                )
                result.add(
                    Contact(
                        id = "882",
                        contact_id = "882",
                        name = "Test Two\n tete\nst11",
                        number = "010-2222-3333",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Two",
                        type = 1
                    )
                )
                result.add(
                    Contact(
                        id = "8821",
                        contact_id = "882",
                        name = "Test Two \n srf324r34 \n 213123",
                        number = "010-2222-2222",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Two",
                        type = 2
                    )
                )
                result.add(
                    Contact(
                        id = "883",
                        contact_id = "8831",
                        name = "Test Three \n 3333 \n 3333 \n 3333 \n 3333",
                        number = "010-3333-4444",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three"
                    )
                )
                result.add(
                    Contact(
                        id = "8831",
                        contact_id = "8831",
                        name = "Test Three\n 33333 \n 344444",
                        number = "010-3333-1111",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 1
                    )
                )
                result.add(
                    Contact(
                        id = "8832",
                        contact_id = "8831",
                        name = "Test Three",
                        number = "010-3333-2222",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 2
                    )
                )
                result.add(
                    Contact(
                        id = "8833",
                        contact_id = "8831",
                        name = "Test Three",
                        number = "010-3333-2223",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 2
                    )
                )
                result.add(
                    Contact(
                        id = "8836",
                        contact_id = "8831",
                        name = "Test Three",
                        number = "010-3333-2224",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 2
                    )
                )
                result.add(
                    Contact(
                        id = "8834",
                        contact_id = "8831",
                        name = "Test Three",
                        number = "010-3333-3333",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 3
                    )
                )
                result.add(
                    Contact(
                        id = "8835",
                        contact_id = "8831",
                        name = "Test Three",
                        number = "010-3333-7777",
                        first_name = "Test",
                        middle_name = "",
                        last_name = "Three",
                        type = 7
                    )
                )
                result.add(
                    Contact(
                        id = "884",
                        contact_id = "884",
                        name = "Test Four\n" +
                                " 4444 \n" +
                                " 344444",
                        number = "010-4444-5555"
                    )
                )

                result.add(
                    Contact(
                        id = "885",
                        contact_id = "885",
                        name = "Sample One motejurhejkjshjsdkjfhskjdehfkehfkjhslkej",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "886",
                        contact_id = "886",
                        name = "Sample Two",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "887",
                        contact_id = "887",
                        name = "Sample Three",
                        number = "031-131"
                    )
                )
                result.add(
                    Contact(
                        id = "888",
                        contact_id = "888",
                        name = "Sample Four",
                        number = "1509"
                    )
                )
                result.add(
                    Contact(
                        id = "889",
                        contact_id = "889",
                        name = "Sample james",
                        number = "031-131-111",
                        first_name = "Sample",
                        middle_name = "One",
                        last_name = "james"
                    )
                )
                result.add(
                    Contact(
                        id = "890",
                        contact_id = "890",
                        name = "Sample james",
                        number = "031-131-222",
                        first_name = "Sample",
                        middle_name = "Two",
                        last_name = "james"
                    )
                )

                result.add(
                    Contact(
                        id = "891",
                        contact_id = "891",
                        name = "james sample",
                        number = "031-131-111",
                        first_name = "james",
                        middle_name = "One",
                        last_name = "Sample"
                    )
                )
                result.add(
                    Contact(
                        id = "892",
                        contact_id = "892",
                        name = "james sample",
                        number = "031-131-222",
                        first_name = "james",
                        middle_name = "Two",
                        last_name = "Sample"
                    )
                )
                result.add(
                    Contact(
                        id = "900",
                        contact_id = "900",
                        name = "Test Five",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "901",
                        contact_id = "901",
                        name = "Test Six",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "902",
                        contact_id = "902",
                        name = "Test Seven\n 7777 \n 77\n77",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "903",
                        contact_id = "903",
                        name = "Test Eight \n 8u8888",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "904",
                        contact_id = "904",
                        name = "Test Nine",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "905",
                        contact_id = "905",
                        name = "Test Ten\n asdasfre34\n5r3245\n234etgfsdg",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "906",
                        contact_id = "906",
                        name = "Test Eleven\n 234234234234 \naa213125121",
                        number = "010-4444-5555"
                    )
                )
                result.add(
                    Contact(
                        id = "907",
                        contact_id = "907",
                        name = "Test Twelve",
                        number = "010-4444-5555"
                    )
                )


                result.forEach {
                    it.nameRmSpecial = convertTextNormalize(
                        languageType.value,
                        it.name
                    )
                }
                _contactsList.value = result
            }

            val jsonData = makeG2PJsonData(_contactsList)
            requestG2P(jsonData)

        }.apply {
            invokeOnCompletion { throwable ->
                CustomLogger.d("[Contact]Job[fetchContacts] invokeOnCompletion this isCancelled ${this.isCancelled}, this isCompleted ${this.isCompleted}, this isActive ${this.isActive}")
                fetchTrackerList.value = this
                when (throwable) {
                    is CancellationException -> CustomLogger.d("Job[fetchContacts] $throwable")
                    else -> CustomLogger.d("[Contact]Job[fetchContacts] is completed without no error")
                }
            }
        }
    }

    fun clearContacts() {
        _contactsList.value.clear()
    }

    fun requestG2P(jsonData: String) {
        CustomLogger.e("start requestG2P")
        // 이미 G2P가 요청된 상태라면 g2p cancel 요청
        checkWaitingPBG2PStateCancel()

        if (jsonData.isNotEmpty()) {
            CustomLogger.d("dataprocessing : $jsonData")
            vrmwManager.g2pController.requestG2P(jsonData)
            isWaitingPBG2PState.value = true
            g2pCompleteCnt[HVRG2PMode.PHONE_BOOK]?.value = -1
        } else {
            CustomLogger.d("dataprocessing is empty")
        }
    }

    fun makeG2PJsonData(contactsList: MutableStateFlow<MutableList<Contact>>): String {
        var jsonData: String = ""

        CustomLogger.e("start makeG2PJsonData coroutine")
        val namelist = mutableListOf<MutableList<String>>()
        CustomLogger.e("addG2p start contactsList size : ${contactsList.value.size}")
        contactsList.value.forEach {
            if (it.first_name.isNotEmpty() && it.last_name.isNotEmpty()) {
//                    CustomLogger.e("addG2p [${it.first_name}], [${it.middle_name}], [${it.last_name}]")

                val name = mutableListOf<String>()
                name.add(it.first_name)
                name.add(it.last_name)
                namelist.add(name)
            } else if (it.first_name.isNotEmpty() || it.last_name.isNotEmpty()) {
                if (it.first_name.isNotEmpty()) {
//                        CustomLogger.e("addG2p first_name [${it.first_name}], [${it.middle_name}], [${it.last_name}]")
                    namelist.add(mutableListOf(it.first_name, ""))
                } else {
//                        CustomLogger.e("addG2p last_name [${it.first_name}], [${it.middle_name}], [${it.last_name}]")
                    namelist.add(mutableListOf("", it.last_name))
                }
            } else {
                if (it.nameRmSpecial.isNotEmpty()) {
//                        CustomLogger.e("addG2p [${it.nameRmSpecial}]")
                    namelist.add(mutableListOf(it.nameRmSpecial, ""))
                }
            }
        }
        CustomLogger.e("addG2p end namelist size : ${namelist.size}")

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        namelist.forEach {
            val jsonArray2 = JSONArray()
            it.forEach {
                jsonArray2.put(it)
            }
            jsonArray.put(jsonArray2)
        }
        if (namelist.isNotEmpty()) {
            jsonObject.put("data", jsonArray)
            jsonObject.put("mode", "phone_book")
            jsonObject.put("device_id", getBluetoothState().hfpDevice.value.device)
            jsonData = jsonObject.toString()
            CustomLogger.d("dataprocessing : $jsonData")
        } else {
            CustomLogger.d("dataprocessing is empty")
        }
        return jsonData
    }

    fun updateACLBluetoothState(address: String, state: Boolean) {
        ACLDevice(address, state)
    }

    fun updateHfpState(address: String, state: Int) {
        coroutineScope.launch(ioDispatcher) {
            when (state) {
                PROFILE_CONNECTED -> {
                    checkWaitingPBG2PStateCancel()
                    getBluetoothState().hfpConnectState.value = state
                    if (!getBluetoothState().hfpDevice.value.recognizing) {
                        getBluetoothState().prevHfpDevice.value = address
                    }
                    HfpDevice(address, true, getBluetoothState().hfpDevice.value.recognizing)
                }

                PROFILE_DISCONNECTED -> {
                    checkWaitingPBG2PStateCancel()

                    if (address == getBluetoothState().hfpDevice.value.device) {
                        _contactsList.emit(mutableListOf())
                        getBluetoothState().hfpConnectState.value = state
                        // 연결 해제 되었을 때 UpdateMobileConnectedState context update를 위해
                        HfpDevice(
                            "",//getBluetoothState().hfpDevice.value.device,
                            false,
                            getBluetoothState().hfpDevice.value.recognizing
                        )
                    } else {
                        getBluetoothState().hfpDevice.value
                    }

                    // Call Disconnection
                    if (!vrmwManager.isIdle()) {
//                        viewModel.currScreen.value?.let { screen ->
//                            CustomLogger.d("updateHfpState screen $screen")
//                            screen.domainType.let { domainType ->
//                                CustomLogger.d("updateHfpState domainType $domainType")
//                                domainType.let {
//                                    if (it == DomainType.Call || it == DomainType.SendMessage) {
//                                        CustomLogger.d("updateHfpState DomainType launchDisconnected $it")
//                                        vrmwManager.stop()
//                                        launchDisconnected(it)
//                                    }
//                                }
//                            }
//                        }
                        /**
                         * 임의로 추가한 코드
                         */
                        vrmwManager.stop()
                        launchDisconnected(DomainType.Call)
                    }

                    getBluetoothState().hfpDevice.value
                }

                else -> getBluetoothState().hfpDevice.value
            }.also {
                CustomLogger.e("[Contact][updateHfpState final address: ${it.device}, connect:${it.connect}, use:${it.recognizing}")
                getBluetoothState().hfpDevice.value = it
            }
        }
    }

    fun updateMapState(address: String, state: Int) {
        when (state) {
            PROFILE_CONNECTED -> {
                getBluetoothState().mapConnectState.value = state
                Pair(address, true)
            }

            PROFILE_DISCONNECTED -> {
                if (address == getBluetoothState().mapConnected.value.first) {
                    getBluetoothState().mapConnectState.value = state
                    Pair("", false)
                } else {
                    getBluetoothState().mapConnected.value
                }

                // Call Disconnection
                if (!vrmwManager.isIdle()) {
                    CustomLogger.d("updateMapState not Idle")
//                    viewModel.currScreen.value?.let { screen ->
//                        CustomLogger.d("updateMapState screen $screen")
//                        screen.domainType.let { domainType ->
//                            CustomLogger.d("updateHfpState domainType $domainType")
//                            domainType.let {
//                                if (it == DomainType.SendMessage) {
//                                    CustomLogger.d("updateHfpState DomainType launchDisconnected $it")
//                                    vrmwManager.stop()
//                                    launchDisconnected(it)
//                                }
//                            }
//                        }
//                    }

                    /**
                     * 임의로 추가한 코드
                     */
                    vrmwManager.stop()
                    launchDisconnected(DomainType.Call)
                }

                getBluetoothState().mapConnected.value
            }

            else -> getBluetoothState().mapConnected.value
        }.also {
            CustomLogger.e("[Contact][updateMapState final address: ${it.first}, connect:${it.second}")
            getBluetoothState().mapConnected.value = it
        }
    }

    fun updateIsContactDownloading(state: PhonebookDownloadState) {
        CustomLogger.d("updateIsContactDownloading $state")
        contactDownloadState.value = state
        //Todo: setContext -> sdk 배포 후
    }

    /**
     * clear contacts
     * 주소록 clear
     * BT Device 삭제 시 app의 주소록 정리
     */
    fun clearContacts(clearDeviceId: String) {
        CustomLogger.d("clearContacts() hfpConnected: ${getBluetoothState().hfpDevice.value}")

        clearJob?.takeIf { it.isActive }?.apply {
            cancel(
                "Job[clearContacts] is cancelled by Manager",
                InterruptedException("Cancelled Forcibly")
            )
        }
        clearJob = coroutineScope.launch(ioDispatcher) {
            if (clearDeviceId == getBluetoothState().hfpDevice.value.device) {
                checkWaitingPBG2PStateCancel()
                // 연결된 device와 삭제된 device가 동일할때 app의 contact data와 server에 업로드하는 data sync
                _contactsList.emit(mutableListOf())
            }
        }.apply {
            invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> CustomLogger.d("Job[clearContacts] $throwable ")
                    else -> CustomLogger.d("[Contact]Job[clearContacts] is completed without no error")
                }
            }
        }
    }

    private fun launchDisconnected(domainType: DomainType) {
        CustomLogger.d("[launchDisconnected] domainType : ${domainType}")

        val noticeModel = NoticeModel().apply {

            if (getBluetoothState().hfpConnectState.value == PROFILE_DISCONNECTED) {
                when (domainType) {
                    DomainType.Call -> {
                        noticeString = context.getString(R.string.TID_SCH_PHON_03_02_1)
                        noticePromptId = "PID_SCH_PHON_03_02_1"
                    }

                    DomainType.SendMessage -> {
                        noticeString = context.getString(R.string.TID_SCH_PHON_03_07)
                        noticePromptId = "PID_SCH_PHON_03_07"
                    }

                    else -> {}
                }
            } else if (getBluetoothState().mapConnectState.value == PROFILE_DISCONNECTED) {
                noticeString = context.getString(R.string.TID_SCH_PHON_14_01)
                noticePromptId = "PID_SCH_PHON_14_01"
            }
        }
//        launchNotice(noticeModel, false)

        changeUiState(Pair(DomainUiState.AnnounceWindow(noticeModel.noticeString), mwContext.value))
        TODO("조건 상태가 맞지 않을 경우 해당 noticeModel의 문구를 출력 시키도록 해야함")
    }

    // 전화번호부 List - 전체 전화번호부 표시
    fun makePhoneBookContactList(): ArrayList<Contact> {
        CustomLogger.e(TAG, "makePhoneBookContactList ")
        val findList = mutableListOf<Contact>()
        for (contact in contactsList.value) {
            findList.add(contact.copy())
        }
        CustomLogger.e("findList Size : ${findList.size}")
        var filterList = findList.filter {
            it.name.isNotEmpty() && it.number.isNotEmpty()
        }
        CustomLogger.e("filterList Size : ${filterList.size}")

        filterList = filterList.distinctBy {
            it.contact_id
        }

        return ArrayList(filterList)
    }

    fun makeContactList(
        items: List<Contact>,
        contactsList: List<Contact>,
        contactId: String = "",
    ): MutableList<Contact> {
        var result = mutableListOf<Contact>()

        if (contactId == "") {
            // 같은 이름 list
            Log.d("sendMsg", "같은 이름 list")
            val filterList = items.distinctBy {
                it.contact_id
            }
            Log.d("sendMsg", "filterList: ${filterList}")
            for (item in filterList) {
                for (contact in contactsList) {
                    if (item.contact_id == contact.contact_id) {
                        Log.d("sub", "${contact}")
                        result.add(contact)
                        break
                    }
                }
            }
        } else {
            Log.d("sendMsg", "같은 카테고리 리스트")
            Log.d("sendMsg", "item : ${items} ")
            val filterList = mutableListOf<Contact>()
            // 같은 카테고리 리스트

            for (item in items) {
                if (item.contact_id == contactId) {
                    filterList.add(item)
                }
            }
            // contactsList 중 filterList가 있는 것만 List로
            Log.d("sendMsg", "filterList : ${filterList} ")
            result = items.filter { it in contactsList }.toMutableList()
            Log.d("sendMsg", "result : ${result} ")
        }

        return result
    }


//    fun makeContactList(
//        items: List<Contact>,
//        filter: Boolean,
//        category: Int = 0,
//        isOtherNumber: Boolean = false
//    ): ArrayList<Contact> {
//        return makeContactList(
//            items,
//            contactsList.value,
//            filter = filter,
//            category = category,
//            isOtherNumber = isOtherNumber
//        )
//    }
//
//    fun makeContactList(
//        items: List<Contact>,
//        contactsList: List<Contact>,
//        filter: Boolean,
//        category: Int = 0,
//        isOtherNumber: Boolean = false
//    ): ArrayList<Contact> {
//        CustomLogger.e("makeContactList find target size : ${items.size}, filter:${filter} category:${category}")
//        items.forEach {
//            CustomLogger.e("find target: ${it}")
//        }
//        val findList = mutableListOf<Contact>()
//        for (item in items) {
//
//            // Other Number인 경우, contact_id가 이미 있는 경우에 해당
//            if (item.contact_id.isNotEmpty()) {
//                CustomLogger.e("find with contact_id: ${item.contact_id}")
//                for (contact in contactsList) {
//                    if (item.contact_id.equals(contact.contact_id, ignoreCase = true)) {
//                        findList.add(contact.copy(slotType = item.slotType))
//                    }
//                }
//            } else if (!Tags.FULL_FIRST.isEqual(item.slotType)) {
//                item.nameRmSpecial.let { name ->
////                        println(
////                            "find :[${name}] slotType:${
////                                Tags.values().find { tag -> tag.value == item.slotType }
////                            }"
////                        )
//                    for (contact in contactsList) {
//                        if (name.equals(contact.nameRmSpecial, ignoreCase = true)) {
////                                println(
////                                    "found:[${name}]: id:${contact.id} contact_id:${contact.contact_id} nameRmSpecial: ${contact.nameRmSpecial} number:${contact.number} contactType:${contact.type} itemType:${item.type}"
////                                )
//                            findList.add(contact.copy(slotType = item.slotType))
//                        }
//                    }
//
//                    if (findList.isEmpty()) {
////                            println(
////                                "not found name using equals"
////                            )
//                        for (contact in contactsList) {
//                            if (contact.nameRmSpecial.isNotEmpty()) {
//                                if (name.contains(contact.nameRmSpecial, ignoreCase = true) ||
//                                    contact.nameRmSpecial.contains(name, ignoreCase = true)
//                                ) {
////                                        println(
////                                            "found:[${name}]: id:${contact.id} contact_id:${contact.contact_id} nameRmSpecial: ${contact.nameRmSpecial} number:${contact.number} contactType:${contact.type} itemType:${item.type}"
////                                        )
//                                    // Call <Name>인 경우는 바로 전화
//                                    // Call <Name, Category>인 경우는 없다고 한 후 다른 Category 번호로 전화해야 함.
//                                    findList.add(contact.copy(slotType = item.slotType))
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
////                    println(
////                        "full_first find f:[${item.trimFirstName()}] l:[${item.trimLastName()}]"
////                    )
//                for (contact in contactsList) {
//                    if (item.trimFirstName().equals(contact.trimFirstName(), ignoreCase = true)
//                        && item.trimLastName().equals(contact.trimLastName(), ignoreCase = true)
//                    ) {
//                        if (item.trimFirstName().isEmpty() && item.trimLastName().isEmpty()) {
//                            continue
//                        }
//                        if (contact.trimFirstName().isEmpty() && contact.trimLastName().isEmpty()) {
//                            continue
//                        }
////                            println(
////                                "full_first found:[${item.trimFirstName()} ${item.trimLastName()}]: id:${contact.id} contact_id:${contact.contact_id} nameRmSpecial: ${contact.nameRmSpecial} number:${contact.number} contactType:${contact.type} itemType:${item.type}"
////                            )
//                        findList.add(contact.copy(slotType = item.slotType))
//                    }
//                }
//                if (findList.isEmpty()) {
////                        println(
////                            "full_first not found name using equals"
////                        )
//                    for (contact in contactsList) {
//                        if (contact.nameRmSpecial.isNotEmpty()) {
//                            if ((item.trimFirstName()
//                                    .contains(contact.trimFirstName(), true) ||
//                                        contact.nameRmSpecial
//                                            .contains(item.trimFirstName(), true))
//                                &&
//                                (item.trimLastName()
//                                    .contains(contact.trimLastName(), true) ||
//                                        contact.nameRmSpecial
//                                            .contains(item.trimLastName(), true))
//                            ) {
//
//                                if (item.trimFirstName().isEmpty() && item.trimLastName()
//                                        .isEmpty()
//                                ) {
//                                    continue
//                                }
//                                if (contact.trimFirstName().isEmpty() && contact.trimLastName()
//                                        .isEmpty()
//                                ) {
//                                    continue
//                                }
//
////                                    println(
////                                        "full_first found:[${item.trimFirstName()}_${item.trimLastName()}]: id:${contact.id} contact_id:${contact.contact_id} nameRmSpecial: ${contact.nameRmSpecial} number:${contact.number} contactType:${contact.type} itemType:${item.type}"
////                                    )
//                                // Call <Name>인 경우는 바로 전화
//                                // Call <Name, Category>인 경우는 없다고 한 후 다른 Category 번호로 전화해야 함.
//                                findList.add(contact.copy(slotType = item.slotType))
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        CustomLogger.e("findList Size : ${findList.size}")
//
//        var filterList = findList.filter {
//            it.name.isNotEmpty() && it.number.isNotEmpty()
//        }
//
//
//        // 카테고리 기준 필터링 시작
//        if (category != 0) {
//
//            // 이름 갯수 구하기
//            // 아더넘버 발화인 경우는 contact_id 가 입력된 하나의 연락처만 입력되기 때문에
//            // 검색조건에서 여러사람이 나올 수 없음
//            val nameCount = filterList.distinctBy {
//                it.contact_id
//            }.size
//            // 맞는 카테고리 갯수 구하기
//            val rowCount = filterList.filter {
//                it.type == category
//            }.size
//
//            // 타입 맞는게 1개 이상이면 카테고리 필터 적용
//            // 여러이름이더라도 카테고리까지 맞는것 우선 리턴
//            // 맞는 카테고리가 없으면 목록 보여줌
//            if (rowCount > 0) {
//                filterList = filterList.filter {
//                    it.type == category
//                }
//            }
//
//            // 여러사람이 있는 경우
//            if (nameCount > 1) {
//                // 여러사람이 카테고리 까지 맞거나, 일치하는 카테고리가 여러개 일때 사람기준으로 압축
//                filterList = filterList.distinctBy {
//                    it.contact_id
//                }
//            } else {
//                // 아더넘버 발화인 경우 사람기준 압축하지 않음
//                if (!isOtherNumber) {
//                    filterList = filterList.distinctBy {
//                        it.contact_id
//                    }
//                }
//            }
//        } else {
//            // 카테고리 검색이 아닌 경우
//            // filter : contact_id 기준으로 중복 제거
//            if (filter) {
//                filterList = filterList.distinctBy {
//                    it.contact_id
//                }
//            } else {
//                filterList = filterList.distinctBy {
//                    it.id
//                }
//            }
//        }
//
//
//        CustomLogger.e("filterList Size : ${filterList.size}")
//        val slicedSubList = filterList.subList(0, minOf(20, filterList.size))
//        CustomLogger.e("slicedSubList Size : ${slicedSubList.size}")
//
//        return ArrayList<Contact>(slicedSubList)
//    }

    fun getMwState(): MWState {
        return mwState
    }

    companion object {
        const val MISSED_CALL = 3
        const val OUTGOING_CALL = 2
        const val INCOMING_CALL = 1

        const val PROFILE_DISCONNECTED = 0
        const val PROFILE_CONNECTING = 1
        const val PROFILE_CONNECTED = 2
        const val PROFILE_DISCONNECTING = 3

        //properties
        private const val PROP_KEY_AA_CONNECTED = "sys.mtx.androidauto.state"
        private const val PROP_KEY_CP_CONNECTED = "sys.mtx.carplay.state"

        init {
            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
        }


    }
}
