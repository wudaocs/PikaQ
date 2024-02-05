package com.whatever.permission.helper

internal class LowApiPermissionsHelper<T>(host: T) : PermissionHelper<T>(host) {
    override fun directRequestPermissions(
        requestCode: Int,
        vararg perms: String,
    ) {
    }

    override fun shouldShowRequestPermissionRationale(perm: String): Boolean = false

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
