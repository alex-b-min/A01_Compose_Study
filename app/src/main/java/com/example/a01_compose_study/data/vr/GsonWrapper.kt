package com.example.a01_compose_study.data.vr

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

object GsonWrapper {
    fun <T> fromJson(result: String, classOfT: Class<T>): T {
//        val convert = appendEscapeCharacters(result)
        return Gson().fromJson(result, classOfT)
    }

    private fun appendEscapeCharacters(input: String): String {
        // 세렌스 서버에서 backslash 를 하나만 보내다보니, GSon으로 fromJson을 할때, escape문자로 인식해서 없어지는 현상이 있어서, 강제로 추가함.
        val builder = StringBuilder(input.length)
        var index = 0

        while (index < input.length) {
            val currentChar = input[index]
            builder.append(currentChar)
            if (currentChar == '\\') {
                // append the escape character
                builder.append('\\')
            }
            index++
        }

        return builder.toString()
    }
}


// GsonBuilder를 사용할때, adapter를 사용수할있음.
class CustomDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): String {
        val value = json?.asString
        val convert = value?.replace("\\", "\\\\") ?: ""
        return convert
    }
}
