package com.whatever.permission.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal class SupportFragmentPermissionHelper(private val host: Fragment) :
    BaseSupportPermissionsHelper<Fragment>(host) {
    override fun getSupportFragmentManager(): FragmentManager {
        return host.childFragmentManager
    }

    override fun directRequestPermissions(
        requestCode: Int,
        vararg perms: String,
    ) {
        host.requestPermissions(perms, requestCode)
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean {
        return host.shouldShowRequestPermissionRationale(perm)
    }
}
