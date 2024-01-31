package com.whatever.block.core

import android.os.Debug
import android.os.SystemClock
import android.util.Printer
import com.whatever.block.BlockCanary
import com.whatever.block.DEFAULT_BLOCK_THRESHOLD_MILLIS
import com.whatever.block.interfaces.BlockListener

class LooperMonitor(
    private val blockListener: BlockListener,
    private val blockThresholdMillis: Long = DEFAULT_BLOCK_THRESHOLD_MILLIS,
    private val stopWhenDebugging: Boolean = false
) : Printer {

    private var mPrintingStarted = false

    private var mStartTimestamp: Long = 0
    private var mStartThreadTimestamp: Long = 0
    override fun println(x: String?) {
        if (stopWhenDebugging && Debug.isDebuggerConnected()) {
            return
        }
        if (mPrintingStarted) {
            val endTime = System.currentTimeMillis()
            mPrintingStarted = false
            if (isBlock(endTime)) {
                notifyBlockEvent(endTime)
            }
            stopDump()
        } else {
            mStartTimestamp = System.currentTimeMillis()
            mStartThreadTimestamp = SystemClock.currentThreadTimeMillis()
            mPrintingStarted = true
            startDump()
        }
    }

    private fun notifyBlockEvent(endTime: Long) {
        val startTime = mStartTimestamp
        val startThreadTime = mStartThreadTimestamp
        val endThreadTime = SystemClock.currentThreadTimeMillis()
        HandlerThreadFactory.getWriteLogThreadHandler()?.post {
            blockListener.onBlockEvent(
                startTime,
                endTime,
                startThreadTime,
                endThreadTime
            )
        }
    }

    private fun isBlock(endTime: Long): Boolean {
        return endTime - mStartTimestamp > blockThresholdMillis
    }

    private fun startDump() {
        BlockCanary.blockCore.stackSampler?.start()
        BlockCanary.blockCore.cpuSampler?.start()
    }

    private fun stopDump() {
        BlockCanary.blockCore.stackSampler?.stop()
        BlockCanary.blockCore.cpuSampler?.stop()
    }

}