package com.whatever.permission.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager

internal class AppCompatActivityPermissionsHelper(private val host: AppCompatActivity) :
    BaseSupportPermissionsHelper<AppCompatActivity>(host) {
    override fun getSupportFragmentManager(): FragmentManager {
        return host.supportFragmentManager
    }

    override fun directRequestPermissions(
        requestCode: Int,
        vararg perms: String,
    ) {
        ActivityCompat.requestPermissions(host, perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(host, perm)
    }
}
