package com.whatever.pikaq

import com.whatever.frame.base.BaseViewModel
import com.whatever.frame.entities.GenerallyListEntity
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * main 功能 viewModel
 */
class MainViewModel : BaseViewModel(){

    private val stateFlow = MutableStateFlow(GenerallyListEntity("0","000"))

    fun updateList(){
        stateFlow.value = GenerallyListEntity("1","111")
    }


}