package com.whatever.block.interfaces

import com.whatever.block.entities.BlockInfo

internal interface BlockInterceptor {
    fun onBlock(blockInfo: BlockInfo)
}