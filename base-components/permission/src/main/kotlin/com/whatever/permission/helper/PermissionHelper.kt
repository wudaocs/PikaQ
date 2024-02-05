package com.whatever.permission.helper

import android.app.Activity
import android.os.Build
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class PermissionHelper<T>(val mHost: T) {
    companion object {
        fun newInstance(host: Activity): PermissionHelper<out Activity> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return LowApiPermissionsHelper(host)
            }
            return if (host is AppCompatActivity) {
                AppCompatActivityPermissionsHelper(host)
            } else {
                ActivityPermissionHelper(host)
            }
        }

        fun newInstance(host: Fragment): PermissionHelper<Fragment> {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                LowApiPermissionsHelper(host)
            } else {
                SupportFragmentPermissionHelper(host)
            }
        }
    }

    fun requestPermissions(
        rationale: String,
        positiveButton: String,
        negativeButton: String,
        theme: Int,
        requestCode: Int,
        vararg perms: String
    ) {
        if (shouldShowRationale(*perms)) {
            showRequestPermissionRationale(
                rationale, positiveButton, negativeButton, theme, requestCode, *perms
            )
        } else {
            directRequestPermissions(requestCode, *perms)
        }
    }

    private fun shouldShowRationale(vararg perms: String): Boolean {
        for (perm in perms) {
            if (shouldShowRequestPermissionRationale(perm)) {
                return true
            }
        }
        return false
    }

    // ============================================================================
    // Public abstract methods
    // ============================================================================
    abstract fun directRequestPermissions(
        requestCode: Int,
        vararg perms: String,
    )

    abstract fun shouldShowRequestPermissionRationale(perm: String): Boolean

    abstract fun showRequestPermissionRationale(
        rationale: String,
        positiveButton: String,
        negativeButton: String,
        @StyleRes theme: Int,
        requestCode: Int,
        vararg perms: String,
    )
}
