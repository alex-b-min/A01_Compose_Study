package com.example.a01_compose_study.data.custom

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Contacts provider
 * JVM 위에 Unit Test 진행이므로, 주소록에 대한 Permission 이 있어야 함
 * VR 앱 실행을 해서, 기본 설정 완료 후에 태블릿, 에뮬레이터, AVN 에서 테스트 해야
 * AVN 빌드는 tsdDebug/tsdRelease 로 variant 설정 필요, 기존 앱 지우고 해야 설치됨
 * OS 에 따라 위험한 앱이라고 경고 팝업이 뜰 수 있음. 이때 앱을 설치 하도록 해야 테스트가 가능함
 * @constructor
 *
 * @param context
 */

class ContactsProvider @Inject constructor(
    @ApplicationContext context: Context,
){
//    val TAG: String? = this.javaClass.simpleName
//    private var contentResolver: ContentResolver = context.contentResolver
////    private val regex = Regex("[^A-Za-z0-9ㄱ-ㅣ가-힣]+")
//
//    fun getValueString(cursor: Cursor, name: String): String {
//        val index = cursor.getColumnIndex(name)
//        //CustomLogger.i("name:${name} index:${index}")
//        return if (index != -1)
//            return cursor.getString(index) ?: ""
//        else
//            ""
//    }
//
//    fun getValueInt(cursor: Cursor, name: String): Int {
//        val index = cursor.getColumnIndex(name)
//        //CustomLogger.i("name:${name} index:${index}")
//        return if (index != -1)
//            return cursor.getInt(index)
//        else
//            -1
//    }
//
//    /**
//     * Fetch downLoaded Phonebook
//     * BTPhone App 에 저장되어 있는 contacts
//     * HFP 연결 > 연락처 허용 > 연락처 다운로드 (Native Contacts DB) > 연락처 허용 해제 > 재부팅 > BTPhone App DB query
//     * @return
//     */
//    @SuppressLint("Range", "Recycle")
//    fun fetchBTPhoneAppPhoneBook(): List<Contact> {
//        CustomLogger.e("fetchBTPhoneAppPhoneBook Start")
//        val result: MutableList<Contact> = mutableListOf()
//
//        /**
//         * ContentResolver
//         * - 이 객체를 사용하여 클라이언트로서 제공자와 통신을 주고받음
//         * - BTPhone App Content URI: content://com.mtx.app.phone.provider.phonebook
//         */
//        try {
//            val contentUri = Uri.parse(ContactConstants.CONTENT_URI)
////        val providerClient = contentResolver.acquireContentProviderClient(contentUri)
//            contentResolver?.let { provider ->
//                CustomLogger.e("fetchBTPhoneAppPhoneBook contentResolver let")
//                var cursor: Cursor? = provider.query(
//                    contentUri,
//                    null, // select *
//                    null, // where 절
//                    null,
//                    ContactConstants.KEY_WESTERN_NAME// order by
//                )
//                cursor?.let {
//                    cursor.moveToFirst()
//                    CustomLogger.e("fetchBTPhoneAppPhoneBook cursor let it.columnCount: ${it.columnCount} provider result:${it.count}")
//                    it.columnNames.forEach { name ->
//                        val exist = it.getColumnIndex(name)
//                        CustomLogger.e("it.columnNames: ${name} exist:${exist}")
//                    }
//                    while (!cursor.isAfterLast) {
//
//                        val _ID = getValueString(
//                            it,
//                            ContactsContract.CommonDataKinds.Phone._ID
//                        )
//                        val CONTACT_ID =
//                            getValueString(
//                                it,
//                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//                            )
//                        val NUMBER =
//                            getValueString(
//                                it,
//                                ContactsContract.CommonDataKinds.Phone.NUMBER
//                            )
//                        val TYPE = getValueInt(
//                            it,
//                            ContactsContract.CommonDataKinds.Phone.TYPE
//                        )
//                        val first_name = getValueString(it, ContactConstants.KEY_FIRST_NAME)
//                        val middle_name = getValueString(it, ContactConstants.KEY_MIDDLE_NAME)
//                        val last_name = getValueString(it, ContactConstants.KEY_LAST_NAME)
//                        val western_name = getValueString(it, ContactConstants.KEY_WESTERN_NAME)
//                        val memory_type = getValueInt(it, ContactConstants.KEY_MEMORY_TYPE)
//
//                        // 내 이름 중복 제거
//                        if (memory_type != 1) {
//                            result.add(
//                                Contact(
//                                    id = _ID,
//                                    contact_id = CONTACT_ID,
//                                    name = western_name,
//                                    nameRmSpecial = convertTextNormalize(
//                                        viewModel.languageType.value,
//                                        western_name
//                                    ),
//                                    number = NUMBER,
//                                    type = TYPE,
//                                    first_name = convertTextNormalize(
//                                        viewModel.languageType.value,
//                                        first_name
//                                    ),
//                                    middle_name = convertTextNormalize(
//                                        viewModel.languageType.value,
//                                        middle_name
//                                    ),
//                                    last_name = convertTextNormalize(
//                                        viewModel.languageType.value,
//                                        last_name
//                                    ),
//                                    memory_type = memory_type
//                                )
//                            )
//                        }
//                        cursor.moveToNext()
//                    }
//                    CustomLogger.e("fetchBTPhoneAppPhoneBook cursor end")
//                }
//                cursor?.close()
//            }
//        } catch (e: Exception) {
//            CustomLogger.e("fetchBTPhoneAppPhoneBook e:$e")
//        }
//
//        CustomLogger.e("fetchBTPhoneAppPhoneBook End result : ${result.size}")
//        return result.toList()
//    }
//
////    /**
////     * Fetch callLog
////     * 최근 전화 목록 리스트 가져옴
////     * @return
////     */
////    @SuppressLint("Range")
////    fun fetchContactsCallLog(): List<ContactCallLog> {
////        val result: MutableList<ContactCallLog> = mutableListOf()
////
////        /**
////         * ContentResolver
////         * - 이 객체를 사용하여 클라이언트로서 제공자와 통신을 주고받음
////         * - Type 값의 경우 수신콜 1, 발신콜 2, 부재중 3 입니다.
////         * - Query 시 기본적으로 Date 내림차순으로 가져옴으로 첫번째 Type 이 2 인 콜이 마지막 발신 콜.
////         */
////        val cursor = contentResolver.query(
////            Uri.parse(ContactConstants.URI_CALL_LOGS),
////            null,
////            null, // where 절
////            null,
////            null
////        )
////
////        try {
////            cursor?.let { c ->
////                while (c.moveToNext()) {
////                    val date: Long = c.getLong(c.getColumnIndex(DATE))
////                    val number: String = c.getString(c.getColumnIndex(NUMBER))
////                    val name: String = c.getString(c.getColumnIndex(NAME)) ?: ""
////                    val type: Int = c.getInt(c.getColumnIndex(TYPE))
////
////                    ContactCallLog(date, number, name, type).also { callLog ->
////                        CustomLogger.d("date:${callLog}, number:${callLog.number}, name:${callLog.name} type:${callLog.type}")
////                        result.add(callLog)
////                    }
////                }
////            }
////        } catch (e: Exception) {
////            CustomLogger.e("fetchContactsCallLog: $e")
////        }
////        cursor?.close()
////
////        return result.toList()
////    }
////
////    /**
////     * Fetch contacts redial CallLog
////     * 전화번호가 있는 리스트만 들고 옴(1개 이지만 확장성을 위해서 리스트로)
////     * @return
////     */
////    @SuppressLint("Range")
////    fun fetchContactsRedialCallLog(): List<ContactCallLog> {
////        val result: MutableList<ContactCallLog> = mutableListOf()
////
////        /**
////         * ContentResolver
////         * - 이 객체를 사용하여 클라이언트로서 제공자와 통신을 주고받음
////         * - Type 값의 경우 수신콜 1, 발신콜 2, 부재중 3 입니다.
////         * - Query 시 기본적으로 Date 내림차순으로 가져옴으로 첫번째 Type 이 2 인 콜이 마지막 발신 콜.
////         */
////        val cursor = contentResolver.query(
////            Uri.parse(ContactConstants.URI_CALL_LOGS),
////            null,
////            "$TYPE = 2", // where 절
////            null,
////            null
////        )
////
////        try {
////            cursor?.let { c ->
////                while (c.moveToNext()) {
////                    val date: Long = c.getLong(c.getColumnIndex(DATE))
////                    val number: String = c.getString(c.getColumnIndex(NUMBER))
////                    val name: String = c.getString(c.getColumnIndex(NAME)) ?: ""
////                    val type: Int = c.getInt(c.getColumnIndex(TYPE))
////
////                    ContactCallLog(date, number, name, type).also { callLog ->
////                        //CustomLogger.d("date:${callLog}, number:${callLog.number}, name:${callLog.name} type:${callLog.type}")
////                        result.add(callLog)
////                    }
////                }
////            }
////        } catch (e: Exception) {
////            CustomLogger.e("fetchContactsCallLog: $e")
////        }
////        cursor?.close()
////
////        return result.toList()
////    }
//
//    @SuppressLint("Range")
//    fun fetchPhoneBookState(): PhoneBookState {
//
//        var result = PhoneBookState()
//        try {
//            val projection = arrayOf(
//                ContactConstants.KEY_HFP_DEVICE,
//                ContactConstants.KEY_PBAP_DEVICE,
//                ContactConstants.KEY_MAP_DEVICE,
//                ContactConstants.KEY_CONTACT_DOWNLOADED,
//                ContactConstants.KEY_CALL_LOG_DOWNLOADED
//            )
//            val selection = null
//            val uri = Uri.parse(ContactConstants.URI_CONNECTED_DEVICE)
//
//            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                contentResolver.query(
//                    uri,
//                    projection,
//                    null,
//                    null
//                )
//            } else {
//                contentResolver.query(
//                    uri,
//                    projection,
//                    selection, // where 절
//                    null,
//                    null
//                )
//            }
//
//            CustomLogger.d("fetchPhoneBookState cursor:$cursor")
//
//            cursor?.let { c ->
//                val bundle: Bundle = c.extras
//                val hfpAddress =
//                    bundle.getString(ContactConstants.KEY_HFP_DEVICE, null).replace(":", "_")
//                val pbapAddress =
//                    bundle.getString(ContactConstants.KEY_PBAP_DEVICE, null).replace(":", "_")
//                val mapAddress =
//                    bundle.getString(ContactConstants.KEY_MAP_DEVICE, null).replace(":", "_")
//                val contactDownloaded =
//                    bundle.getBoolean(ContactConstants.KEY_CONTACT_DOWNLOADED, false)
//                val callLogDownloaded =
//                    bundle.getBoolean(ContactConstants.KEY_CALL_LOG_DOWNLOADED, false)
//
//                PhoneBookState(
//                    hfpAddress,
//                    pbapAddress,
//                    mapAddress,
//                    contactDownloaded,
//                    callLogDownloaded
//                ).also { pbState ->
//                    CustomLogger.d("fetchPhoneBookState hfpAddress: ${pbState.hfpDevice}, pbapDevice: ${pbState.pbapDevice}, mapAddress: ${pbState.mapDevice} contactDownloaded: ${pbState.contactDownloaded}")
//                    result = pbState
//                }
//            }
//
//            cursor?.close()
//            CustomLogger.d("fetchPhoneBookState close:$result")
//
//
//        } catch (e: Exception) {
//            CustomLogger.e("fetchPhoneBookState: $e")
//            e.message?.let { CustomLogger.e(it) }
//        }
//
//        return result
//
//    }
//
//    companion object {
//        const val DATE = "date"
//        const val NUMBER = "number"
//        const val TYPE = "type"
//        const val NAME = "name"
//
//        init {
//            CustomLogger.i("Constructor companion object Hash[${this.hashCode()}]")
//        }
//    }
//
//    init {
//        CustomLogger.i("Constructor Hash[${this.hashCode()}]")
//        CustomLogger.i("TestMode register before")
//        TestMode.registerObserver(TAG, this)
//        CustomLogger.i("TestMode register after")
//    }
//
//    override fun notifyObserver(data: TestData) {
//        CustomLogger.i("notifyObserver before resolver : $contentResolver, after resolver : ${data.resolver}")
//        contentResolver = data.resolver
//    }
//
//    override fun close() {
//        CustomLogger.i("TestMode unregister before")
//        TestMode.unRegisterObserver(TAG, this)
//        CustomLogger.i("TestMode unregister after")
//    }
}