package com.whatever.permission.helper

import android.app.Activity
import androidx.core.app.ActivityCompat

internal class ActivityPermissionHelper(private val host: Activity) : PermissionHelper<Activity>(host) {
    override fun directRequestPermissions(
        requestCode: Int,
        vararg perms: String,
    ) {
        ActivityCompat.requestPermissions(host, perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(host, perm)
    }

    override fun showRequestPermissionRationale(
        rationale: String,
        positiveButton: String,
        negativeButton: String,
        theme: Int,
        requestCode: Int,
        vararg perms: String,
    ) {
    }
}
