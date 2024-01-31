package com.whatever.block.entities

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Build.VERSION
import android.text.TextUtils
import com.whatever.block.BLANK
import com.whatever.block.BlockCanary
import com.whatever.block.KEY_API
import com.whatever.block.KEY_CPU_BUSY
import com.whatever.block.KEY_CPU_CORE
import com.whatever.block.KEY_CPU_RATE
import com.whatever.block.KEY_FREE_MEMORY
import com.whatever.block.KEY_MODEL
import com.whatever.block.KEY_NETWORK
import com.whatever.block.KEY_PROCESS
import com.whatever.block.KEY_QUA
import com.whatever.block.KEY_STACK
import com.whatever.block.KEY_THREAD_TIME_COST
import com.whatever.block.KEY_TIME_COST
import com.whatever.block.KEY_TIME_COST_END
import com.whatever.block.KEY_TIME_COST_START
import com.whatever.block.KEY_TOTAL_MEMORY
import com.whatever.block.KEY_UID
import com.whatever.block.KEY_VERSION_CODE
import com.whatever.block.KEY_VERSION_NAME
import com.whatever.block.KV
import com.whatever.block.SEPARATOR
import com.whatever.block.utils.getFreeMemory
import com.whatever.block.utils.getNumCores
import com.whatever.block.utils.getTotalMemory
import com.whatever.block.utils.myProcessName
import java.io.Serializable

@Suppress("DEPRECATION")
internal class BlockInfo : Serializable {
    private val blockModel = Build.MODEL

    private var qualifier: String? = null

    var timeCost: Long = 0
    var threadTimeCost: Long = 0
    var timeStart: String? = null
    var timeEnd: String? = null

    var cpuBusy = false

    var cpuRateInfo: String? = null

    private var versionName = ""
    private var versionCode = 0

    private var uid: String? = null
    private var network: String? = null
    private var processName: String? = null
    private var freeMemory: String? = null
    private var totalMemory: String? = null

    var threadStackEntries = ArrayList<String>()

    private val basicSb = java.lang.StringBuilder()
    private val cpuSb = java.lang.StringBuilder()
    private val timeSb = java.lang.StringBuilder()
    private val stackSb = java.lang.StringBuilder()

    init {
        if (versionName.isEmpty()) {
            try {
                val context = BlockCanary.getContext()
                val info: PackageInfo? =
                    context?.packageManager?.getPackageInfo(context.packageName, 0)
                versionCode = info?.versionCode ?: 0
                versionName = info?.versionName ?: ""
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        uid = BlockCanary.getConfig().provideUid()
        network = BlockCanary.getConfig().provideNetworkType()
        processName = myProcessName()
        freeMemory = getFreeMemory().toString()
        totalMemory = getTotalMemory().toString()
    }

    private fun appendContent(
        stringBuilder: StringBuilder,
        key: String,
        value: String?,
        isSeparator: Boolean = true,
    ) {
        if (!TextUtils.isEmpty(value)) {
            stringBuilder.append(key).append(KV).append(value)
            if (isSeparator) {
                stringBuilder.append(SEPARATOR)
            } else {
                stringBuilder.append(BLANK)
            }
        }
    }

    fun flushString(): BlockInfo {
        val separator: String = SEPARATOR
        appendContent(basicSb, KEY_QUA, qualifier)
        appendContent(basicSb, KEY_VERSION_NAME, versionName, false)
        appendContent(basicSb, KEY_VERSION_CODE, versionCode.toString(), false)
        appendContent(basicSb, KEY_UID, uid, false)
        appendContent(basicSb, KEY_NETWORK, network, false)
        appendContent(basicSb, KEY_MODEL, blockModel, false)
        appendContent(basicSb, KEY_API, VERSION.SDK_INT.toString() + " " + VERSION.RELEASE)
        appendContent(basicSb, KEY_CPU_CORE, getNumCores().toString(), false)
        appendContent(basicSb, KEY_PROCESS, processName, false)
        appendContent(basicSb, KEY_FREE_MEMORY, freeMemory, false)
        appendContent(basicSb, KEY_TOTAL_MEMORY, totalMemory)
        appendContent(timeSb, KEY_TIME_COST, timeCost.toString())
        appendContent(timeSb, KEY_THREAD_TIME_COST, threadTimeCost.toString())
        appendContent(timeSb, KEY_TIME_COST_START, timeStart)
        appendContent(timeSb, KEY_TIME_COST_END, timeEnd)
        appendContent(cpuSb, KEY_CPU_BUSY, cpuBusy.toString(), false)
        appendContent(cpuSb, KEY_CPU_RATE, cpuRateInfo)
        if (threadStackEntries.isNotEmpty()) {
            val temp = StringBuilder()
            for (s in threadStackEntries) {
                temp.append(s)
                temp.append(separator)
            }
            if (temp.isNotEmpty()) {
                stackSb.append(separator)
                stackSb.append(KEY_STACK).append(KV).append(temp.toString())
                    .append(separator)
            }
        }
        return this
    }

    override fun toString(): String {
        return basicSb.toString() + timeSb + cpuSb + stackSb
    }
}
