package com.whatever.frame.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import java.lang.ref.WeakReference

var application: Application? = getApplicationBySelf()

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
private fun getApplicationBySelf(): Application? {
    try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val method = activityThreadClass.getMethod("currentActivityThread", *arrayOfNulls(0))
        val currentAT = method.invoke(null)
        val appField = activityThreadClass.getDeclaredField("mInitialApplication")
        appField.isAccessible = true
        application = appField[currentAT] as Application
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return application
}


/**
 * 获取String类型的meta标签
 */
@Suppress("unused")
fun getMetaDataByString(key: String): String = getMeta()?.getString(key) ?: ""

/**
 * 获取Int类型的meta标签
 */
@Suppress("unused")
fun getMetaDataByInt(key: String): Int = getMeta()?.getInt(key) ?: 0

/**
 * 获取Boolean类型的meta标签 默认为false
 */
@Suppress("unused")
fun getMetaDataByBoolean(key: String): Boolean = getMeta()?.getBoolean(key, false) ?: false

// 当期应用的包名
val appPackageName = application?.applicationContext?.packageName ?: "getAppPackageName exception"

private var weakReference: WeakReference<Bundle>? = null

/**
 * 调整为缓存数据 减少每次调用所需要的耗时
 */
@Suppress("DEPRECATION")
private fun getMeta(): Bundle? {
    return weakReference?.get() ?: run {
        val meta = application?.packageManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getApplicationInfo(
                    appPackageName,
                    PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                getApplicationInfo(appPackageName, PackageManager.GET_META_DATA)
            }
        }
        weakReference = WeakReference<Bundle>(meta?.metaData)
        return weakReference?.get()
    }
}

val loggerEnable: Int = getMetaDataByInt("LOG_ENABLE")