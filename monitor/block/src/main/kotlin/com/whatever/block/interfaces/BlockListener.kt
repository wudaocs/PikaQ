package com.whatever.block.interfaces

interface BlockListener {
    fun onBlockEvent(
        realStartTime: Long,
        realTimeEnd: Long,
        threadTimeStart: Long,
        threadTimeEnd: Long
    )
}