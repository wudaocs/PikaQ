package com.whatever.block.sampler

import android.os.Process
import android.util.Log
import com.whatever.block.BUFFER_SIZE
import com.whatever.block.MAX_ENTRY_COUNT
import com.whatever.block.SEPARATOR
import com.whatever.block.TIME_FORMATTER
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class CpuSampler(samplerInterval: Long) : AbstractSampler(samplerInterval) {

    private val mCpuInfoEntries = LinkedHashMap<Long, String>()

    private var mPid = 0
    private var mUserLast: Long = 0
    private var mSystemLast: Long = 0
    private var mIdleLast: Long = 0
    private var mIoWaitLast: Long = 0
    private var mTotalLast: Long = 0
    private var mAppCpuTimeLast: Long = 0

    override fun start() {
        super.start()
        reset()
    }

    override fun doSampler() {
        var cpuReader: BufferedReader? = null
        var pidReader: BufferedReader? = null

        try {
            cpuReader = BufferedReader(
                InputStreamReader(
                    FileInputStream("/proc/stat")
                ), BUFFER_SIZE
            )
            var cpuRate = cpuReader.readLine()
            if (cpuRate == null) {
                cpuRate = ""
            }
            if (mPid == 0) {
                mPid = Process.myPid()
            }
            pidReader = BufferedReader(
                InputStreamReader(
                    FileInputStream("/proc/$mPid/stat")
                ), BUFFER_SIZE
            )
            var pidCpuRate = pidReader.readLine()
            if (pidCpuRate == null) {
                pidCpuRate = ""
            }
            parse(cpuRate, pidCpuRate)
        } catch (throwable: Throwable) {
            Log.e("CpuSampler", "doSample: ", throwable)
        } finally {
            try {
                cpuReader?.close()
                pidReader?.close()
            } catch (exception: IOException) {
                Log.e("CpuSampler", "doSample: ", exception)
            }
        }
    }

    private fun getBusyTime() = (mSamplerInterval * 1.2f).toInt()

    /**
     * Get cpu rate information
     *
     * @return string show cpu rate information
     */
    fun getCpuRateInfo(): String {
        val sb = StringBuilder()
        synchronized(mCpuInfoEntries) {
            for ((time, value) in mCpuInfoEntries) {
                sb.append(TIME_FORMATTER.format(time))
                    .append(' ')
                    .append(value)
                    .append(SEPARATOR)
            }
        }
        return sb.toString()
    }

    fun isCpuBusy(start: Long, end: Long): Boolean {
        if (end - start > mSamplerInterval) {
            val s: Long = start - mSamplerInterval
            val e: Long = start + mSamplerInterval
            var last: Long = 0
            synchronized(mCpuInfoEntries) {
                for ((time) in mCpuInfoEntries) {
                    val busyTime = getBusyTime()
                    if (time in (s + 1) until e) {
                        if (last != 0L && time - last > busyTime) {
                            return true
                        }
                        last = time
                    }
                }
            }
        }
        return false
    }

    private fun reset() {
        mUserLast = 0
        mSystemLast = 0
        mIdleLast = 0
        mIoWaitLast = 0
        mTotalLast = 0
        mAppCpuTimeLast = 0
    }

    private fun parse(cpuRate: String, pidCpuRate: String) {
        val cpuInfoArray = cpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (cpuInfoArray.size < 9) {
            return
        }
        val user = cpuInfoArray[2].toLong()
        val nice = cpuInfoArray[3].toLong()
        val system = cpuInfoArray[4].toLong()
        val idle = cpuInfoArray[5].toLong()
        val ioWait = cpuInfoArray[6].toLong()
        val total = (user + nice + system + idle + ioWait
                + cpuInfoArray[7].toLong() + cpuInfoArray[8].toLong())
        val pidCpuInfoList = pidCpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (pidCpuInfoList.size < 17) {
            return
        }
        val appCpuTime =
            pidCpuInfoList[13].toLong() + pidCpuInfoList[14].toLong() + pidCpuInfoList[15].toLong() + pidCpuInfoList[16].toLong()
        if (mTotalLast != 0L) {
            val stringBuilder = java.lang.StringBuilder()
            val idleTime = idle - mIdleLast
            val totalTime = total - mTotalLast
            stringBuilder
                .append("cpu:")
                .append((totalTime - idleTime) * 100L / totalTime)
                .append("% ")
                .append("app:")
                .append((appCpuTime - mAppCpuTimeLast) * 100L / totalTime)
                .append("% ")
                .append("[")
                .append("user:").append((user - mUserLast) * 100L / totalTime)
                .append("% ")
                .append("system:").append((system - mSystemLast) * 100L / totalTime)
                .append("% ")
                .append("ioWait:").append((ioWait - mIoWaitLast) * 100L / totalTime)
                .append("% ]")
            synchronized(mCpuInfoEntries) {
                mCpuInfoEntries[System.currentTimeMillis()] = stringBuilder.toString()
                if (mCpuInfoEntries.size > MAX_ENTRY_COUNT) {
                    for ((key) in mCpuInfoEntries) {
                        mCpuInfoEntries.remove(key)
                        break
                    }
                }
            }
        }
        mUserLast = user
        mSystemLast = system
        mIdleLast = idle
        mIoWaitLast = ioWait
        mTotalLast = total
        mAppCpuTimeLast = appCpuTime
    }
}