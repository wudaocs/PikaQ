package com.whatever.frame.utils

import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * log printer
 */
@Suppress("unused")
object L {
    private val isDebug: Boolean = loggerEnable == 0
    private val TAG = L.javaClass.name

    init {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun v(msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.v(TAG, msg)
            }
        }
    }

    fun vl(tag: String = TAG, msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.v(tag, msg)
            }
        }
    }

    fun d(msg: Any? = null) {
        if (isDebug) {
            msg?.run {
                Log.d(TAG, msg.toString())
            }
        }
    }

    fun d(msg: String? = null) {
        dl(TAG, msg)
    }

    fun dl(tag: String = TAG, msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.d(tag, msg)
            }
        }
    }

    fun e(msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Logger.e(msg)
            }
        }
    }

    fun el(tag: String = TAG, msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.e(tag, msg)
            }
        }
    }

    fun i(msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Logger.i(msg)
            }
        }
    }

    fun il(tag: String = TAG, msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.i(tag, msg)
            }
        }
    }

    fun w(msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Logger.i(msg)
            }
        }
    }

    fun wl(tag: String = TAG, msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Log.w(tag, msg)
            }
        }
    }

}

