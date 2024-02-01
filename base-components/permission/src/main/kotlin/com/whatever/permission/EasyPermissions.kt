package com.whatever.permission

import android.os.Build

class EasyPermissions {
    fun hasPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        }
    }
}
