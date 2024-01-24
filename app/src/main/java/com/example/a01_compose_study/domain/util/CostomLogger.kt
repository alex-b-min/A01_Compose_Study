package com.example.a01_compose_study.domain.util

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun StackTraceElement.generateMessage(msg: Any = "") = "$methodName() Line $lineNumber: $msg"

object CustomLogger {
    var isUnitTest: Boolean = false
    var m_appId: String = "VRHMI"
    var m_tagId: String = "VRHMI"
    var enabled = true
    var useReflection = true

    val m_formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")

    init {
//        i("CustomLogger Hash[${this.hashCode()}]")
    }

    enum class LogLevel {
        Debug,
        Info,
        Error
    }

    private fun print(level: LogLevel, tag: String, message: String) {

        val t = tag.ifEmpty { m_tagId }
        if (useReflection) {
            var refactMessage = StringBuilder()
            val current = LocalDateTime.now().format(m_formatter)
            val curThreadId = Thread.currentThread().name

            // reflection을 사용하면 된다고하는데, 이건 비용이 크다고함. dev일때에만 사용하고 release일경우에는 출력안하게 해야함
            val ste = Thread.currentThread().stackTrace[4]
            val cClass = ste.fileName ?: ste.className?.split(".")?.last()

            val cFunc = ste.methodName
            val cLine = ste.lineNumber
            refactMessage.clear()
            refactMessage.append("[$current][$curThreadId:$curThreadId][$cClass:$cLine:$cFunc] $message")

            if (isUnitTest) {
                println(refactMessage)
            } else {
                level.let { l ->
                    when (l) {
                        LogLevel.Info -> {
                            Log.i(t, refactMessage.toString())
                        }

                        LogLevel.Debug -> {
                            Log.d(t, refactMessage.toString())
                        }

                        LogLevel.Error -> {
                            Log.e(t, refactMessage.toString())
                        }

                        else -> {
                            Log.d(t, refactMessage.toString())
                        }
                    }
                }
            }

        } else {

            level.let { l ->
                when (l) {
                    LogLevel.Info -> {
                        Log.i(t, message)
                    }

                    LogLevel.Debug -> {
                        Log.d(t, message)
                    }

                    LogLevel.Error -> {
                        Log.e(t, message)
                    }

                    else -> {
                        Log.d(t, message)
                    }
                }
            }

        }


    }

    fun setUnittest() {
        isUnitTest = true
    }

    fun d(tag: String, message: String) {
        print(LogLevel.Debug, tag, message)
    }

    fun d(message: String) {
        print(LogLevel.Debug, "", message)
    }


    fun i(tag: String, message: String) {
        print(LogLevel.Info, tag, message)
    }

    fun i(message: String) {
        print(LogLevel.Info, "", message)
    }

    fun e(tag: String, message: String) {
        print(LogLevel.Error, tag, message)
    }

    fun e(message: String) {
        print(LogLevel.Error, "", message)
    }
}
