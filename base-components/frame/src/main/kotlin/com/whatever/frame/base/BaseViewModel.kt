package com.whatever.frame.base

import androidx.lifecycle.ViewModel

/**
 * viewModel 基类
 */
abstract class BaseViewModel : ViewModel(){


    override fun onCleared() {
        super.onCleared()
    }

}