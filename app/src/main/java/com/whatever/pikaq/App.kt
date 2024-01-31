package com.whatever.pikaq

import android.app.Application
import com.whatever.block.BlockCanary
import com.whatever.block.interfaces.IBlockConfigs

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        BlockCanary.install(this).configure(object : IBlockConfigs {
            override fun blockThreshold(): Long {
                return 200
            }
        })
    }
}