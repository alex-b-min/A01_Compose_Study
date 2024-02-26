package com.example.a01_compose_study.data.custom

import com.example.a01_compose_study.data.Contact
import com.example.a01_compose_study.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(
//    private val source: ContactsProvider,
    private val dummySource: ContactsDummyProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchContacts(): List<Contact> =
        withContext(ioDispatcher) {
            /**
             * 앱의 폰 북 더미 데이터 가져오기
             */
//            listOf()
            dummySource.fetchContacts()
//            source.fetchBTPhoneAppPhoneBook()
        }

//    suspend fun fetchContactsCallLog(): List<ContactCallLog> =
//        withContext(ioDispatcher) {
//            if (com.ftd.ivi.cerence.BuildConfig.FLAVOR.startsWith("app")) {
//                dummySource.fetchContactsCallLog()
//            } else {
//                source.fetchContactsCallLog()
//            }
//        }

//    suspend fun fetchContactsRedialCallLog(): List<ContactCallLog> =
//        withContext(ioDispatcher) {
//            if (com.ftd.ivi.cerence.BuildConfig.FLAVOR.startsWith("app")) {
//                dummySource.fetchContactsRedialCallLog()
//            } else {
//                source.fetchContactsRedialCallLog()
//            }
//        }

    suspend fun fetchPhoneBookState(): PhoneBookState =
        withContext(ioDispatcher) {
            /**
             * 앱의 폰 북 더미 데이터 동기화하기
             */
            dummySource.fetchPhoneBookState()
//            source.fetchPhoneBookState()
        }
}