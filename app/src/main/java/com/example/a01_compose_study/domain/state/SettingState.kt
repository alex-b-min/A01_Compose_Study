package com.example.a01_compose_study.domain.state

import com.example.a01_compose_study.data.SystemData
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class StatePropertyType {
    NONE,
    INT,
    STRING,
    MAX
}

enum class StateVariableType {
    NONE,
    NORMAL,
    MUTABLE,
    MAX
}

data class StateDataField(
    val key: String,
    val propertyType: StatePropertyType,
    val varType: StateVariableType
)

open class BaseState() {
    protected var bundle = HashMap<String, Any>()
    protected var mutableBundle = HashMap<String, MutableStateFlow<Any>>()


    fun setValue(key: String, value: Any, varType: StateVariableType = StateVariableType.NORMAL) {
        if (StateVariableType.MUTABLE == varType) {
            mutableBundle[key]?.value = value
            CustomLogger.i("mutableBundle - Key[$key], value[${mutableBundle[key]?.value}], isMutableSet[$varType]")
        } else {
            bundle[key] = value
            CustomLogger.i("Key[$key], value[${bundle[key]}], isMutableSet[$varType]")
        }
    }

    protected fun <T : Any> getValue(key: String, defaultValue: T): T {
        var retValue: T = defaultValue
        try {
            defaultValue::class.javaObjectType.cast(bundle[key])?.let {
                retValue = it
            }
        } catch (e: Exception) {
            CustomLogger.e("getValue Failed key[$key] e:$e")
            e.printStackTrace()
        }
        CustomLogger.i("Key[$key] SaveData [${bundle[key]}] RetValue [$retValue]")
        return retValue
    }

    override fun toString(): String {
        var mutableStr = ""
        mutableBundle.forEach {
            mutableStr += "${it.key}=${it.value.value}, "
        }
        return "Bundle - ${bundle}, MutableBundle - ${mutableStr}, hash - ${super.toString()}"
    }
}

class SettingState() : BaseState() {
    companion object {
        // Static하게 사용하기위한 데이터.
        val settingDataField = arrayListOf(
            StateDataField(
                SystemData.MTX_MODEM_INFO_IMEI.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_MOBILE_CARRIER_NAME.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_NSM_SESSION_STATE.value,
                StatePropertyType.INT,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_PROV_DATA_USER_ID.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_PROV_DATA_USER_PW.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_PROV_DATA_SERVICE_EXPIRY.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_TEMPERATURE_UNIT.value,
                StatePropertyType.STRING,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_CALL_STATE.value,
                StatePropertyType.STRING,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_RECENT_OUTGOING_CALL_NUMBER.value,
                StatePropertyType.STRING,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_CURRENT_PACKAGE.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_ANTENNA_LEVEL.value,
                StatePropertyType.INT,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_MODEM_INFO_NADID.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_MODEM_INFO_ICCID.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_MODEM_INFO_IMSI.value,
                StatePropertyType.STRING,
                StateVariableType.NORMAL
            ),
            StateDataField(
                SystemData.MTX_BT_PAIRED_DEVICE_SIZE.value,
                StatePropertyType.INT,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_CURRENT_MEDIA.value,
                StatePropertyType.STRING,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_CURRENT_USER_IDX.value,
                StatePropertyType.INT,
                StateVariableType.MUTABLE
            ),
            StateDataField(
                SystemData.MTX_PROV_DATA_URL.value,
                StatePropertyType.STRING,
                StateVariableType.MUTABLE
            )
        )
    }

    init {
        settingDataField.forEach {
            if (it.varType == StateVariableType.MUTABLE) {
                mutableBundle[it.key] = MutableStateFlow(
                    if (it.propertyType == StatePropertyType.INT) {
                        0
                    } else {
                        ""
                    }
                )
                CustomLogger.i("Setting mutableBundle init - key[${it.key}], value[${mutableBundle[it.key]?.value}]")
            } else {
                bundle[it.key] = if (it.propertyType == StatePropertyType.INT) {
                    0
                } else {
                    ""
                }
                CustomLogger.i("Setting bundle init - key[${it.key}], value[${bundle[it.key]}]")
            }
        }

    }

    // 관찰할필요없는 경우 일반 변수로 선언
    val imei: String
        get() {
            return getValue(SystemData.MTX_MODEM_INFO_IMEI.value, "")
        }
    val carrierName: String
        get() {
            return getValue(SystemData.MTX_MOBILE_CARRIER_NAME.value, "")
        }
    val userId: String
        get() {
            return getValue(SystemData.MTX_PROV_DATA_USER_ID.value, "")
        }
    val userPW: String
        get() {
            return getValue(SystemData.MTX_PROV_DATA_USER_PW.value, "")
        }
    val serviceExpiry: String
        get() {
            return getValue(SystemData.MTX_PROV_DATA_SERVICE_EXPIRY.value, "")
        }
    val currentPackage: String
        get() {
            return getValue(SystemData.MTX_CURRENT_PACKAGE.value, "")
        }
    val nadid: String
        get() {
            return getValue(SystemData.MTX_MODEM_INFO_NADID.value, "")
        }
    val iccid: String
        get() {
            return getValue(SystemData.MTX_MODEM_INFO_ICCID.value, "")
        }
    val imsi: String
        get() {
            return getValue(SystemData.MTX_MODEM_INFO_IMSI.value, "")
        }

    val provDataUrl: String
        get() {
            return getValue(SystemData.MTX_PROV_DATA_URL.value, "")
        }

    // 관찰할 필요 있는 경우 stateFlow사용
    val temperatureUnit =
        mutableBundle[SystemData.MTX_TEMPERATURE_UNIT.value]!!.asStateFlow() as StateFlow<String>
    val callState =
        mutableBundle[SystemData.MTX_CALL_STATE.value]!!.asStateFlow() as StateFlow<String>
    val recentOutgoingCallNumber =
        mutableBundle[SystemData.MTX_RECENT_OUTGOING_CALL_NUMBER.value]!!.asStateFlow() as StateFlow<String>
    val antennaLevel =
        mutableBundle[SystemData.MTX_ANTENNA_LEVEL.value]!!.asStateFlow() as StateFlow<Int>
    val btPairedDeviceSize =
        mutableBundle[SystemData.MTX_BT_PAIRED_DEVICE_SIZE.value]!!.asStateFlow() as StateFlow<Int>
    val currentMedia =
        mutableBundle[SystemData.MTX_CURRENT_MEDIA.value]!!.asStateFlow() as StateFlow<String>
    val userIdx =
        mutableBundle[SystemData.MTX_CURRENT_USER_IDX.value]!!.asStateFlow() as StateFlow<Int>

    // 만약에 직접 set해야할 필요가 있는경우엔 Mutable로 다시 바라보도록...
    val offlineMode =
        mutableBundle[SystemData.MTX_NSM_SESSION_STATE.value]!! as MutableStateFlow<Int>

    fun isOfflineMode(): Boolean {
        return offlineMode.value <= 0
    }

    /***
     * 개통여부 체크
     * 231031 : Raul 말씀으로는 개통이 된 상태여야만 시스템에서 VR에 이벤트를 전달 해 줄 것이기 때문에
     * 개통여부 체크와 VR 동의 체크는 하지 않아도 된다고 함
     * offline 모드 자체도 개통된 상태라고 함
     */
    fun isCCUSubscribed(): Boolean {
        //return offlineMode.value >= 1
        return true
    }

    fun isOnlineVR(): Boolean {
        return offlineMode.value == 2
    }

}