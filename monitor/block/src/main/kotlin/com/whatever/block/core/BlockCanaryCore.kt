package com.whatever.block.core

import android.os.Environment
import android.os.Looper
import android.util.Log
import com.whatever.block.BlockCanary
import com.whatever.block.TIME_FORMATTER
import com.whatever.block.entities.BlockInfo
import com.whatever.block.sampler.CpuSampler
import com.whatever.block.sampler.StackSampler
import com.whatever.block.utils.LogWriter
import com.whatever.block.interfaces.BlockInterceptor
import com.whatever.block.interfaces.BlockListener
import java.io.File
import java.io.FilenameFilter
import java.util.LinkedList

internal class BlockCanaryCore {
    var mMonitor: LooperMonitor? = null
    var stackSampler: StackSampler? = null
    var cpuSampler: CpuSampler? = null

    private val mLogWriter = LogWriter()

    private val mInterceptorChain: List<BlockInterceptor> = LinkedList<BlockInterceptor>()

    init {
        stackSampler =
            StackSampler(
                Looper.getMainLooper().thread,
                samplerIntervalMillis = BlockCanary.getConfig().provideDumpInterval(),
            )

        cpuSampler = CpuSampler(BlockCanary.getConfig().provideDumpInterval())

        setMonitor(
            LooperMonitor(
                object : BlockListener {
                    override fun onBlockEvent(
                        realStartTime: Long,
                        realTimeEnd: Long,
                        threadTimeStart: Long,
                        threadTimeEnd: Long,
                    ) {
                        // Get recent thread-stack entries and cpu usage
                        val tsEntries =
                            stackSampler?.getThreadStackEntries(realStartTime, realTimeEnd)

                        if (!tsEntries.isNullOrEmpty()) {
                            // 生成一个block
                            val block =
                                BlockInfo().apply {
                                    timeCost = realTimeEnd - realStartTime
                                    threadTimeCost = threadTimeEnd - threadTimeStart
                                    timeStart = TIME_FORMATTER.format(realStartTime)
                                    timeEnd = TIME_FORMATTER.format(realTimeEnd)

                                    cpuBusy = cpuSampler?.isCpuBusy(realStartTime, realTimeEnd) ?: false
                                    cpuRateInfo = cpuSampler?.getCpuRateInfo()
                                    threadStackEntries = tsEntries
                                }.flushString()
                            Log.e("卡顿信息", block.toString())
                            if (mInterceptorChain.isNotEmpty()) {
                                mInterceptorChain.forEach {
                                    it.onBlock(block)
                                }
                            }
                        }
                    }
                },
                BlockCanary.getConfig().blockThreshold(),
                BlockCanary.getConfig().stopWhenDebugging(),
            ),
        )

        mLogWriter.cleanObsolete()
    }

    private fun setMonitor(looperPrinter: LooperMonitor) {
        mMonitor = looperPrinter
    }

    fun getPath(): String {
        val state = Environment.getExternalStorageState()
        val logPath =
            if (BlockCanary.getContext() == null) "" else BlockCanary.getConfig().providePath()
        return if (Environment.MEDIA_MOUNTED == state &&
            Environment.getExternalStorageDirectory()
                .canWrite()
        ) {
            Environment.getExternalStorageDirectory().path + logPath
        } else {
            "${BlockCanary.getContext()?.filesDir}${BlockCanary.getConfig().providePath()}"
        }
    }

    fun detectedBlockDirectory(): File {
        val directory: File = File(getPath())
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun getLogFiles(): Array<File>? {
        val f: File = detectedBlockDirectory()
        return if (f.exists() && f.isDirectory) {
            f.listFiles(BlockLogFileFilter())
        } else {
            null
        }
    }

    private class BlockLogFileFilter : FilenameFilter {
        override fun accept(
            dir: File,
            filename: String,
        ): Boolean {
            return filename.endsWith(".log")
        }
    }
}
