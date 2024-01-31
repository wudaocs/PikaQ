package com.whatever.block.interfaces

/**
 * 配置信息
 */
interface IBlockConfigs {

    fun provideQualifier() = "version_flavor"

    fun provideUid() = "uid"

    fun provideNetworkType() = "unknown"

    fun provideMonitorDuration() = -1L

    fun blockThreshold() = 1000L

    fun provideDumpInterval() = blockThreshold()

    fun providePath() = "/blockcanary/"

    fun displayNotification() = true

    fun provideWhiteList() : List<String> = listOf("org.chromium")

    fun stopWhenDebugging() = true


}