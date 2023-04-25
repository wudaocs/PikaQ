package com.whatever.frame.base

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * 所有页面的基类
 */
abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    abstract fun setCompose(parent: CompositionContext? = null,
//                            content: @Composable () -> Unit)

}