package com.example.a01_compose_study.data.custom

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun Context.deviceId(): String? {
    return NetworkUtils.macAddress ?: Settings.Secure.getString(
        this.contentResolver,
        Settings.Secure.ANDROID_ID,
    )
}
