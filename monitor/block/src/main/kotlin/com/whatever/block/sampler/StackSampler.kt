package com.whatever.block.sampler

import com.whatever.block.DEFAULT_MAX_ENTRY_COUNT
import com.whatever.block.SEPARATOR
import com.whatever.block.TIME_FORMATTER


/**
 * 栈采样器
 */
class StackSampler(
    private val thread: Thread,
    private val maxEntryCount: Int = DEFAULT_MAX_ENTRY_COUNT,
    samplerIntervalMillis: Long
) : AbstractSampler(samplerIntervalMillis) {

    private val sStackMap = LinkedHashMap<Long, String>()

    fun getThreadStackEntries(startTime: Long, endTime: Long): ArrayList<String>? {
        val result = ArrayList<String>()
        synchronized(sStackMap) {
            for (entryTime in sStackMap.keys) {
                if (entryTime in (startTime + 1) until endTime) {
                    result.add(
                        (TIME_FORMATTER.format(entryTime)
                                + SEPARATOR
                                + SEPARATOR
                                ) + sStackMap[entryTime]
                    )
                }
            }
        }
        return result
    }

    override fun doSampler() {

        val stringBuilder = StringBuilder()

        for (stackTraceElement in thread.stackTrace) {
            stringBuilder
                .append(stackTraceElement.toString())
                .append(SEPARATOR)
        }

        synchronized(sStackMap) {
            if (sStackMap.size == maxEntryCount && maxEntryCount > 0) {
                sStackMap.remove(sStackMap.keys.iterator().next())
            }
            sStackMap.put(
                System.currentTimeMillis(),
                stringBuilder.toString()
            )
        }
    }


}