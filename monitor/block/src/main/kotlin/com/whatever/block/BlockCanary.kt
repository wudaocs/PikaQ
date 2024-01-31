package com.whatever.block

import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import com.whatever.block.core.BlockCanaryCore
import com.whatever.block.interfaces.IBlockConfigs

/**
 * 配置文件中设置是否开启检查,true 开启，false 关闭
 *  <meta-data
 *      android:name="monitor_block_enable"
 *      android:value="true" />
 */
object BlockCanary {
    private var mApplication: Application? = null

    private var mParams: IBlockConfigs =
        object : IBlockConfigs {
            override fun blockThreshold(): Long {
                return 800
            }
        }

    internal val blockCore: BlockCanaryCore by lazy { BlockCanaryCore() }

    private var mMonitorStarted = false

    fun install(application: Application?): BlockCanary {
        if (getMonitorEnable(application)) {
            mApplication = application
            // 自动开启检测
            start()
        }
        return this
    }

    // 配置
    fun configure(configs: IBlockConfigs): BlockCanary {
        mParams = configs
        return this
    }

    private fun start() {
        if (!mMonitorStarted) {
            mMonitorStarted = true
            Looper.getMainLooper().setMessageLogging(blockCore.mMonitor)
        }
    }

    fun stop() {
        if (mMonitorStarted) {
            mMonitorStarted = false
            Looper.getMainLooper().setMessageLogging(null)
            blockCore.stackSampler?.stop()
            blockCore.cpuSampler?.stop()
        }
    }

    internal fun getConfig() = mParams

    private fun getMonitorEnable(application: Application?): Boolean =
        application?.let {
            it.packageManager?.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
        }?.metaData?.getBoolean("monitor_block_enable", false) ?: false

    internal fun getContext() = mApplication?.applicationContext
}
