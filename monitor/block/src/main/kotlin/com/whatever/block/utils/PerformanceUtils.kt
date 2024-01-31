package com.whatever.block.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.util.Log
import com.whatever.block.BlockCanary
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

private var blockCoreNum = 0
private var blockTotalMemo: Long = 0

private var sProcessName: String? = null
private val sNameLock = Any()

fun getNumCores(): Int {
    if (blockCoreNum == 0) {
        blockCoreNum =
            try {
                // Get directory containing CPU info
                val dir = File("/sys/devices/system/cpu/")
                // Filter to only list the devices we care about
                val files = dir.listFiles { pathname -> Pattern.matches("cpu[0-9]", pathname.name) }
                // Return the number of cores (virtual CPU devices)
                files?.size ?: 1
            } catch (e: Exception) {
                1
            }
    }
    return blockCoreNum
}

fun getFreeMemory(): Long {
    val am = BlockCanary.getContext()?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val mi = ActivityManager.MemoryInfo()
    am?.getMemoryInfo(mi)
    return mi.availMem / 1024
}

fun getTotalMemory(): Long {
    if (blockTotalMemo == 0L) {
        val str1 = "/proc/meminfo"
        val str2: String?
        val arrayOfString: Array<String>
        var initialMemory: Long = -1
        var localFileReader: FileReader? = null
        try {
            localFileReader = FileReader(str1)
            val localBufferedReader = BufferedReader(localFileReader, 8192)
            str2 = localBufferedReader.readLine()
            if (str2 != null) {
                arrayOfString =
                    str2.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                initialMemory = Integer.valueOf(arrayOfString[1]).toLong()
            }
            localBufferedReader.close()
        } catch (e: IOException) {
            Log.e(
                "getTotalMemory exception = ",
                e.toString(),
            )
        } finally {
            if (localFileReader != null) {
                try {
                    localFileReader.close()
                } catch (e: IOException) {
                    Log.e(
                        "close localFileReader exception = ",
                        e.toString(),
                    )
                }
            }
        }
        blockTotalMemo = initialMemory
    }
    return blockTotalMemo
}

fun myProcessName(): String? {
    if (sProcessName != null) {
        return sProcessName
    }
    synchronized(sNameLock) {
        if (sProcessName != null) {
            return sProcessName
        }
        sProcessName = obtainProcessName(BlockCanary.getContext())
        return sProcessName
    }
}

private fun obtainProcessName(context: Context?): String? {
    val pid = Process.myPid()
    val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
    val listTaskInfo = am?.runningAppProcesses
    if (!listTaskInfo.isNullOrEmpty()) {
        for (info in listTaskInfo) {
            if (info != null && info.pid == pid) {
                return info.processName
            }
        }
    }
    return null
}
