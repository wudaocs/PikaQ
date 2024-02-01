package com.whatever.permission.interfaces

interface RationaleCallbacks {
    fun onRationaleAccepted(requestCode: Int)

    fun onRationaleDenied(requestCode: Int)
}
