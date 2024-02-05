import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.util.Log
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * log printer
 */
object L {
    private val TAG = "LogPrint"
    private var isDebug: Boolean = false
    fun buildLogger(context: Context) {
        isDebug = getDebugState(context)
    }

    private fun getDebugState(context: Context): Boolean {
        val meta = context.packageManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getApplicationInfo(
                    context.packageName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            }
        }
        return meta?.metaData?.getInt("LOG_ENABLE") == 0
    }

    init {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun v(msg: String? = null) {
        if (isDebug) {
            msg?.run {
                Logger.v(TAG, msg)
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
                Logger.d(msg.toString())
            }
        }
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

