package com.example.a01_compose_study.presentation.util

import com.example.a01_compose_study.domain.model.BaseApplication
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

object StringManager {

    var listPCM = MutableStateFlow(listOf<File>())
    private val sttString = MutableStateFlow("")
    fun getPcmList(): List<File> {
        ///data/user/10/com.example.ndkconnectapp/files/VRMW/test/vrmanager/pcm
        val context = BaseApplication.appContext

        val builtinList = listFilesInDirectory("${context.filesDir}/VRMW/test/vrmanager/pcm")
        val developFile = listFilesInDirectory("${context.filesDir}/developPcm")
        val sumList = developFile.toMutableList()
        sumList.addAll(builtinList)
        listPCM.value = sumList.toList()
        return listPCM.value
    }

    fun listFilesInDirectory(path: String): List<File> {
        val directory = File(path)
        val result = mutableListOf<File>()
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()

            files?.let {
                for (file in it) {
                    if (file.isDirectory) {
                        println("Directory: ${file.name}")
                        result.add(file)
                    } else {
                        println("File: ${file.name}")
                        result.add(file)
                    }
                }
            }
        } else {
            println("Specified path either doesn't exist or is not a directory.")
        }
        return result.sorted()
    }

    fun printSttString(str: String) {
        CustomLogger.i("printSttString [$str]")
        // RadioStationGarbage
        if (!str.contains("<channel name>")) {
            sttString.value = str
        }
    }
}