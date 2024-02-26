package com.example.a01_compose_study.data.custom

import android.content.ContentResolver
import android.content.Context
import com.example.a01_compose_study.data.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsDummyProvider @Inject constructor(
    @ApplicationContext context: Context
) {
    private val contentResolver: ContentResolver = context.contentResolver
    val deviceId = context.deviceId()

    fun fetchContacts(): List<Contact> {
        val result: MutableList<Contact> = mutableListOf()
        result.add(Contact(id = "1", name = "문재민", number = "010-1111-2222"))
        result.add(Contact(id = "2", name = "문제민", number = "010-2222-3333"))
        result.add(Contact(id = "3", name = "혜원 9838", number = "010-3333-4444"))
        result.add(Contact(id = "4", name = "Alex", number = "010-4444-5555"))
        result.add(Contact(id = "5", name = "Alexander Sandor", number = "010-4444-5555"))
        result.add(Contact(id = "6", name = "포티투닷 이순신", number = "010-4444-5555"))
        result.add(Contact(id = "7", name = "포티투닷 홍길동 책임연구원", number = "031-131"))
        result.add(Contact(id = "8", name = "엄마", number = "1509"))
        result.add(Contact(id = "9", name = "김혜원 어머님", number = "010-1111-5555"))
        result.add(Contact(id = "10", name = "홍길 동사무소", number = "010-4444-5555"))
        result.add(Contact(id = "11", name = "홍길동 (HMC 유럽)", number = "010-4444-5555"))
        result.add(Contact(id = "12", name = "강신부", number = "010-4444-5555"))
        result.add(Contact(id = "13", name = "우리♥︎", number = "010-4444-5555"))
        result.add(Contact(id = "14", name = "ㅇ ㅏ ㅇ ㅣ ㄷ ㅣ", number = "010-4444-5555"))
        result.add(Contact(id = "15", name = "1096119838", number = "010-4444-5555"))
        result.add(Contact(id = "16", name = "119 장난전화", number = "1509"))
        result.add(Contact(id = "17", name = "이일구", number = "02-131"))

        return result.toList()
    }

//    @SuppressLint("Range")
//    fun fetchContactsCallLog(): List<ContactCallLog> {
//        val result = mutableListOf<ContactCallLog>()
//
//        val cursor = contentResolver.query(
//            CallLog.Calls.CONTENT_URI,
//            null,
//            null, // where 절
//            null,
//            null
//        )
//
//        try {
//            cursor?.let { c ->
//                while (c.moveToNext()) {
//                    val date: Long = c.getLong(c.getColumnIndex(CallLog.Calls.DATE))
//                    val number: String = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))
//                    val name: String =
//                        c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME)) ?: ""
//                    val type: Int = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE))
//
//                    ContactCallLog(date, number, name, type).also { callLog ->
//                        CustomLogger.d("date:${callLog}, number:${callLog.number}, name:${callLog.name} type:${callLog.type}")
//                        result.add(callLog)
//                    }
//                }
//            }
//            CustomLogger.d("fetchContactsCallLog :$cursor")
//
//        } catch (e: Exception) {
//            CustomLogger.e("fetchContactsCallLog: $e")
//        }
//        cursor?.close()
//        CustomLogger.d("fetchContactsCallLog toList:${result.toList()}")
//
//        return result.toList()
//    }

//    @SuppressLint("Range")
//    fun fetchContactsRedialCallLog(): List<ContactCallLog> {
//        val result = mutableListOf<ContactCallLog>()
//        val projection = arrayOf(
//            CallLog.Calls.DATE,
//            CallLog.Calls.NUMBER,
//            CallLog.Calls.CACHED_NAME,
//            CallLog.Calls.TYPE
//        )
//        val selection = "${CallLog.Calls.TYPE} = 2"
//
//        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val queryArgs = bundleOf(
//                ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(CallLog.Calls.DATE),
//                ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
//                ContentResolver.QUERY_ARG_LIMIT to 1,
//                ContentResolver.QUERY_ARG_SQL_SELECTION to selection
//            )
//
//            contentResolver.query(
//                CallLog.Calls.CONTENT_URI,
//                projection,
//                queryArgs,
//                null
//            )
//
//        } else {
//            contentResolver.query(
//                CallLog.Calls.CONTENT_URI,
//                projection,
//                selection, // where 절
//                null,
//                "date DESC LIMIT 1"
//            )
//        }
//
//        try {
//            cursor?.let { c ->
//                while (c.moveToNext()) {
//                    val date: Long = c.getLong(c.getColumnIndex(CallLog.Calls.DATE))
//                    val number: String = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))
//                    val name: String =
//                        c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME)) ?: ""
//                    val type: Int = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE))
//
//                    ContactCallLog(date, number, name, type).also { callLog ->
//                        CustomLogger.d("date:${callLog}, number:${callLog.number}, name:${callLog.name} type:${callLog.type}")
//                        result.add(callLog)
//                    }
//                }
//            }
//            CustomLogger.d("fetchContactsRedialCallLog :$cursor")
//
//        } catch (e: Exception) {
//            CustomLogger.e("fetchContactsRedialCallLog: $e")
//        }
//        cursor?.close()
//        CustomLogger.d("fetchContactsRedialCallLog toList:${result.toList()}")
//
//        return result.toList()
//    }

    fun fetchPhoneBookState(): PhoneBookState {
        return PhoneBookState()
    }
}

