package com.example.a01_compose_study.data.custom

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.a01_compose_study.data.Tags
import com.example.a01_compose_study.domain.util.CustomLogger
import java.net.NetworkInterface
import java.util.Collections
import java.util.UUID


object NetworkUtils {
    val TAG : String? = this.javaClass.simpleName

    init {
        CustomLogger.i("$TAG Constructor Hash[${this.hashCode()}]")
    }

    @JvmStatic
    fun isAvailableNetwork(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            CustomLogger.e("Cant access network state - cause : has no permission")
            return false
        }

        with(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isAvailableNetworkSDKQ()
            } else {
                isAvailableNetworkSDK()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun ConnectivityManager.isAvailableNetworkSDKQ(): Boolean {
        return (getNetworkCapabilities(activeNetwork)?.let {
            when {
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true

                else -> false
            }
        } ?: false).apply {
            CustomLogger.i("network available [SDK-Q] : $this")
        }

    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun ConnectivityManager.isAvailableNetworkSDK(): Boolean {
        val isAvailable = activeNetworkInfo?.isConnected ?: false
        CustomLogger.i("network available : $isAvailable")
        return isAvailable
    }

    //handle exception
    @JvmStatic
    val macAddress: String?
        get() {
            try {
                val all: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    CustomLogger.i("NetworkInterface name - ${nif.name}")
                    if (!Tags.Wlan0.isEqual(nif.name) && !Tags.Eth0.isEqual(nif.name)) continue
                    val macBytes = nif.hardwareAddress ?: return null
                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X", b))
                    }
                    if (res1.isNotEmpty()) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
                //handle exception
            }
            CustomLogger.i("deviceId is null")
            return null
        }

    @JvmStatic
    val getUUID: String get() = UUID.randomUUID().toString()
}