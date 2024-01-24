package com.example.a01_compose_study.domain

import android.annotation.SuppressLint
import android.os.Build
import android.os.UserHandle
import android.os.UserManager
import androidx.annotation.RequiresApi
import com.example.a01_compose_study.data.UserProfileNum
import com.example.a01_compose_study.domain.util.CustomLogger

@SuppressLint("NewApi")
fun UserManager.getUserProfileHandle(): UserHandle? =
    takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.N }?.getUserProfileHandleN()

@RequiresApi(Build.VERSION_CODES.N)
private fun UserManager.getUserProfileHandleN(): UserHandle? =
    userProfiles.firstOrNull { isUserRunning(it) }
        ?.let { profile -> getUserForSerialNumber(profile.hashCode().toLong()) }

@SuppressLint("NewApi", "LogNotTimber")
fun UserManager.getUserId(): Int {
    try {
        var userId = -1
        val userHandles: List<UserHandle> = userProfiles
        for (userHandle in userHandles) {
            var isUserRunning: Boolean = isUserRunning(userHandle)
            CustomLogger.i("isUserRunning : $isUserRunning")
            if (isUserRunning) {
                userId = userHandle.hashCode()
                CustomLogger.i("getUserId userId : $userId")
            }
        }
        CustomLogger.i(
            "getUserId getSerialNumberForUser getUserProfileHandle : ${
                getSerialNumberForUser(
                    getUserProfileHandle()
                ).toInt()
            }"
        )

        return userId
//        return getSerialNumberForUser(getUserProfileHandle()).toInt()
    } catch (e: NullPointerException) {
        CustomLogger.i("BaseManager : $e")
    }
    return 0
}

@SuppressLint("NewApi", "LogNotTimber")
fun UserManager.getProfileNum(): Int {
    return when (getUserId()) {
        UserProfileNum.Profile1.num -> 1
        UserProfileNum.Profile2.num -> 2
        UserProfileNum.Profile3.num -> 3
        else -> 0
    }
}