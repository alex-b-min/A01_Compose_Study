package com.example.a01_compose_study.data.custom

/**
 * Phone book state
 *
 * @property hfpDevice 연결된 device 의 Mac Address, 없다면 Null
 * @property pbapDevice 연결된 device 의 Mac Address, 없다면 Null
 * @property mapDevice 연결된 device 의 Mac Address, 없다면 Null
 * @property contactDownloaded true : Phonebook 다운로드 완료, false: 다운로드 중 혹은 실패
 * @property callLogDownloaded false : true Log다운로드 완료, false: 다운로드 중 혹은 실패
 * @constructor Create empty Phone book state
 */
data class PhoneBookState(
    val hfpDevice: String? = null,
    val pbapDevice: String? = null,
    val mapDevice: String? = null,
    val contactDownloaded: Boolean = false,
    val callLogDownloaded: Boolean = false
)
