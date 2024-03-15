package com.example.a01_compose_study.data.custom.call

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media3.common.BuildConfig
import com.example.a01_compose_study.domain.util.CustomLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BtCall @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG : String? = this.javaClass.simpleName

    fun outgoingCall(phoneNumber: String) {
        Log.d("@@ 전화 겁니다", "Yes")
    }

//    fun outgoingCall(tel: String) {
//        outGoingCallByBt(tel)
//    }
//
//    private fun outGoingCallByBt(tel: String) {
//        try {
//            Intent(ReceiveAction.ACTION_PBV_CALL.action, Uri.parse("tel:$tel")).apply {
//                component = when (BuildConfig.INSTALL_TYPE) {
//                    "A01" -> ComponentName(
//                        "com.mtx.pbvservice",
//                        "com.mtx.pbvservice.receiver.BTPhoneReceiver"
//                    )
//
//                    else -> ComponentName(
//                        "com.mtx.app.phone",
//                        "com.mtx.app.phone.receiver.BTPhoneReceiver"
//                    )
//                }
//            }.also {
//                context.sendHMIBroadcast(it)
//            }
//        } catch (e: Exception) {
//            CustomLogger.e("avnOutGoingCall Exception")
//        }
//    }
//
    fun sendMessage(name: String, phoneNumber: String, message: String) {
        sendMessageByBt(name, phoneNumber, message)
    }

    private fun sendMessageByBt(name: String, phoneNumber: String, message: String) {
//        try {
//            Intent(ReceiveAction.ACTION_PBV_SEND_SMS.action).apply {
//                when (BuildConfig.INSTALL_TYPE) {
//                    "A01" -> setPackage("com.mtx.pbvservice")
//                    else -> setPackage("com.mtx.app.phone")
//                }
//
//                putExtra("display_name", name)
//                putExtra("phone_number", phoneNumber)
//                putExtra("message_body", message)
//            }.also {
//                context.sendHMIBroadcast(it)
//            }
//        } catch (e: Exception) {
//            CustomLogger.e("avnOutGoingCall Exception")
//        }
    }

    // 1001(Call Log), 1003(Contact), 1004(Dial), 1005(Message)
    fun requestBtPhoneAppRun(tabId: Int) {
        Log.d("sendMsg","requestBtPhoneAppRun")
//        try {
//            Intent(ReceiveAction.ACTION_PBV_REQUEST_BTPHONE_OPEN.action).apply {
//                when (BuildConfig.INSTALL_TYPE) {
//                    "A01" -> setPackage("com.mtx.pbvservice")
//                    else -> setPackage("com.mtx.app.phone")
//                }
//
//                val tabSystemId = when (tabId) {
//                    0 -> 1001
//                    1 -> 1003
//                    2 -> 1004
//                    3 -> 1005
//                    else -> 1001
//                }
//                putExtra("tab_id", tabSystemId)
//            }.also {
//                context.sendHMIBroadcast(it)
//            }
//        } catch (e: Exception) {
//            CustomLogger.e("avnOutGoingCall Exception")
//        }
    }

    companion object {
        private const val INTENT_MSG_SENT_ACTION = "SMS_SENT_ACTION"
        private const val INTENT_MSG_DELIVERED_ACTION = "SMS_DELIVERED_ACTION"

        init {
            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
        }
    }
}