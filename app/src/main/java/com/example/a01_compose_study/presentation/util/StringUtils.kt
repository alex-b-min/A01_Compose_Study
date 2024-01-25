package com.example.a01_compose_study.presentation.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StringUtils {
    private const val DATE_FORMAT = "mm:ss"

    @JvmStatic
    val EMPTY: String get() = ""

    @JvmStatic
    fun EDITH_CA_TAG(tag: String?): String = "[EDITH-CA]$tag"

    // TODO : EIC LOG
    //@JvmStatic
    //fun EDITH_TAG(tag: String?): String = "[EDITH]$tag"

    @JvmStatic
    fun getDistanceString(distanceMeter: Int): String {
        return getDistanceString(distanceMeter, false)
    }

    @JvmStatic
    fun getArrivalDisplayTime(secTime: Long): String? {
        val time = System.currentTimeMillis() + secTime * 1000L
        val df = SimpleDateFormat("a h:mm", Locale.KOREA)
        return df.format(Date(time))
    }

    @JvmStatic
    fun getArrivalTime(secTime: Int): String {
        val time = System.currentTimeMillis() + secTime * 1000L
        val df = SimpleDateFormat("yyyyMMddHHmm", Locale.KOREA)
        return df.format(Date(time))
    }

    @JvmStatic
    private fun getDistanceString(distanceMeter: Int, space: Boolean): String {
        var distance = distanceMeter
        var distStr: String?
        if (distance >= 1000) {
            val km = distance.toFloat() / 1000.0f
            distStr = if (km >= 10.0f) {
                String.format("%d%skm", km.toInt(), if (space) " " else "")
            } else {
                String.format("%.1f%skm", distance.toFloat() / 1000.0f, if (space) " " else "")
            }
        } else {
            if (distance <= 0) {
                distance = 0
            }
            distStr = String.format("%d%sm", distance / 10 * 10, if (space) " " else "")
        }
        return distStr
    }

    @JvmStatic
    fun get24HourString(timeMilli: Long): String {
        val df = SimpleDateFormat("HH:mm", Locale.KOREA)
        return df.format(Date(timeMilli))
    }

    @JvmStatic
    fun getMusicTimeString(millis: Long): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date(millis)).replaceFirst("0", "")
    }

    @JvmStatic
    fun getTollFareString(tollFare: Int): String {
        val decimal = DecimalFormat("###,###")
        return decimal.format(tollFare)
    }

    @JvmStatic
    fun getSystemTime(): String {
        val now: Long = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR"))
        return dateFormat.format(date)
    }

    @JvmStatic
    fun getStringTime(timeMilli: Long): String {
        val date = Date(timeMilli)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR"))
        return dateFormat.format(date)
    }

    @JvmStatic
    fun invalidVIN(value: String?): Boolean {
        return value.isNullOrEmpty() || value == "FFFFFFFFFFFFFFFFF" || value.toInt() == 0
    }

    @JvmStatic
    fun bytesToHex(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (byte in bytes) {
            builder.append(String.format("0x%02X ", byte))
        }
        return builder.toString().trim() // 마지막 공백 제거
    }

}