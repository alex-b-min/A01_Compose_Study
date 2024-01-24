package com.example.a01_compose_study.data.repositoryImpl

import androidx.lifecycle.ViewModel
import com.example.a01_compose_study.domain.util.CustomLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

abstract class JobManager : ViewModel() {

    private val jobMap = ConcurrentHashMap<Int, JobData>()

    private val jobDeque = ConcurrentLinkedDeque<JobData>()
    private var runningJob: JobData? = null

    fun addSyncJob(keyString: String? = null, block: suspend CoroutineScope.() -> Unit) {
        val jobData = JobData().apply {
            this.job = CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
                block.invoke(this)
            }
            this.jobString = keyString
        }
        jobDeque.add(jobData)
        procSyncJob()
    }

    fun procSyncJob() {

        if (runningJob == null) {
            runningJob = jobDeque.pollFirst()
            runningJob?.let {
                it.job?.let { job ->
                    job.invokeOnCompletion { throwable ->
                        when (throwable) {
                            is CancellationException -> CustomLogger.d("Job[${it.jobString}] $throwable")
                            else -> CustomLogger.d("Job[${it.jobString}] is completed without no error")
                        }
                        runningJob = null
                        procSyncJob()
                    }
                    job.start()
                }
            }
        }
    }


    fun addJob(
        keyString: String? = null,
        clear: Boolean? = false,
        interruptable: Boolean? = false,
        cancelInterruptable: Boolean? = true,
        block: suspend CoroutineScope.() -> Unit,
    ): JobData {
        val job = CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
        return addJob(
            newJob = job,
            clear = clear,
            keyString = keyString,
            interruptable = interruptable,
            cancelInterruptable = cancelInterruptable
        )
    }

    fun addJob(
        oldJob: JobData? = null,
        clear: Boolean? = false,
        interruptable: Boolean? = false,
        cancelInterruptable: Boolean? = true,
        block: suspend CoroutineScope.() -> Unit,
    ): JobData {
        val job = CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
        return addJob(
            newJob = job,
            oldJob = oldJob,
            clear = clear,
            interruptable = interruptable,
            cancelInterruptable = cancelInterruptable
        )
    }

    fun addJob(
        clear: Boolean? = false,
        interruptable: Boolean? = false,
        cancelInterruptable: Boolean? = true,
        block: suspend CoroutineScope.() -> Unit,
    ): JobData {
        val job = CoroutineScope(Dispatchers.Default).launch(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
        return addJob(
            newJob = job,
            clear = clear,
            interruptable = interruptable,
            cancelInterruptable = cancelInterruptable
        )
    }

    fun addJob(
        newJob: Job,
        keyString: String? = null,
        oldJob: JobData? = null,
        clear: Boolean? = false,
        interruptable: Boolean? = false,
        cancelInterruptable: Boolean? = true
    ): JobData {

        if (cancelInterruptable == true) {
            cancelInterruptable(keyString)
        }
        clear?.let {
            if (it)
                clearJob()
        }
        oldJob?.let {
            removeJob(it)
        }
        keyString?.let { it ->
            removeJob(it)
        }

        val jobData = JobData().apply {
            this.job = newJob
            this.jobString = keyString
            this.isInterruptable = interruptable ?: false
        }
        jobMap[jobData.key] = jobData

        CustomLogger.i("jobAdded : ${jobData.key} ${jobData.jobString}")
        newJob.start()
        newJob.invokeOnCompletion {
            removeJob(jobData.key)
            when (it) {
                is CancellationException -> {
                    CustomLogger.i("jobCancelled : ${jobData.key} ${jobData.jobString}")
                }

                else -> {
                    it?.let {
                        CustomLogger.i("jobException : ${jobData.key} ${jobData.jobString}")
                        it.printStackTrace()
                    } ?: {
                        CustomLogger.i("jobComplete : ${jobData.key} ${jobData.jobString}")
                    }
                }
            }
        }
        return jobData
    }

    fun printJobMap(from: String) {
        CustomLogger.e("----print jobMap from ${from}")
        if (jobMap.isNotEmpty()) {
            CustomLogger.e("    jobMap size: ${jobMap.size} ")
            jobMap.onEachIndexed { index, entry ->
                CustomLogger.e("        [${index}] jobStr:[${entry.value.jobString}] interruptable:${entry.value.isInterruptable}")
            }
        } else {
            CustomLogger.e("    jobMap size: [isEMPTY]")
        }
    }

    fun removeJob(keyString: String? = null) {
        keyString?.let {
            findJob(it)?.let { jobData ->
                CustomLogger.i("removeJob by keyString:${it}")
                jobMap.remove(jobData.key)?.cancel()
            }
        }
    }

    fun removeJob(oldJob: JobData? = null) {
        oldJob?.let {
            CustomLogger.i("removeJob by oldJob:${it.key}")
            oldJob.cancel()
            jobMap.remove(it.key)
        }
    }

    fun removeJob(key: Int) {
        jobMap.remove(key)?.let {
            CustomLogger.i("removeJob by key:${key}")
            it.cancel()
        }
    }

    fun cancelInterruptable(keyString: String?) {
        jobMap.forEach {
            if (it.value.isInterruptable) {
                CustomLogger.i("job interruptable:${it.value.key} ${it.value.jobString} canceled by ${keyString}")
                it.value.cancel()
            }
        }
    }

    fun findJob(jobString: String? = null, key: Int? = null, job: Job? = null): JobData? {
        key?.let {
            jobMap.get(it)?.let {
                return it
            }
        }

        jobMap.forEach { jobData ->
            jobData.value.job?.let {
                if (it.hashCode() == job?.hashCode()) {
                    return jobData.value
                }
            }
            jobData.value.jobString?.let { value ->
                if (value == jobString) {
                    return jobData.value
                }
            }
        }
        return null
    }

    fun clearJob() {
        jobMap.forEach {
            try {
                it.value.let { job ->
                    job.cancel()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        jobMap.clear()
    }

}

class JobData {
    val key = this.hashCode()
    var job: Job? = null
    var jobString: String? = null
    var isInterruptable = false
    fun cancel() {
        try {
            if (job?.isCancelled == true) {
                CustomLogger.e("[$key]is Already Cancelled")
                return
            }
            if (job?.isCompleted == true) {
                CustomLogger.e("[$key]is Already Completed")
                return
            }
            CustomLogger.e("[$key]isActive:${job?.isActive}")
            job?.cancelChildren()
            job?.cancel()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}