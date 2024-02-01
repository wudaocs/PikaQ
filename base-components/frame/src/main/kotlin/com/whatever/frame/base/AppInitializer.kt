package com.whatever.frame.base

import android.content.Context
import androidx.startup.Initializer

class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {

    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        emptyList<Class<Initializer<*>>>().toMutableList()
}