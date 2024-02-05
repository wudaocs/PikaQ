package com.whatever.logger

import android.content.Context
import androidx.startup.Initializer

class LoggerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        L.buildLogger(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        emptyList<Class<Initializer<*>>>().toMutableList()
}