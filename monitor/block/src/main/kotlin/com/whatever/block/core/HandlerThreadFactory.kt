package com.whatever.block.core

import android.os.Handler
import android.os.HandlerThread

internal object HandlerThreadFactory {
    private val sLoopThread: HandlerThreadWrapper =
        HandlerThreadWrapper("loop")
    private val sWriteLogThread: HandlerThreadWrapper =
        HandlerThreadWrapper("writer")


    fun getTimerThreadHandler(): Handler? {
        return sLoopThread.getHandler()
    }

    fun getWriteLogThreadHandler(): Handler? {
        return sWriteLogThread.getHandler()
    }

}

internal class HandlerThreadWrapper(threadName: String) {

    private var handler: Handler? = null

    init {
        val handlerThread = HandlerThread("monitor-block-$threadName")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun getHandler(): Handler? {
        return handler
    }

}