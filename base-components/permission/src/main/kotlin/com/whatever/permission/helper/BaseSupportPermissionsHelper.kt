package com.whatever.permission.helper

import androidx.annotation.StyleRes
import androidx.fragment.app.FragmentManager

internal abstract class BaseSupportPermissionsHelper<T>(host: T) : PermissionHelper<T>(host) {
    abstract fun getSupportFragmentManager(): FragmentManager

    override fun showRequestPermissionRationale(
        rationale: String,
        positiveButton: String,
        negativeButton: String,
        @StyleRes theme: Int,
        requestCode: Int,
        vararg perms: String,
    ) {
//        val fm = getSupportFragmentManager()
//        // Check if fragment is already showing
//        val fragment = fm.findFragmentByTag(RationaleDialogFragmentCompat.TAG)
//        if (fragment is RationaleDialogFragmentCompat) {
//            Log.d(
//                BaseSupportPermissionsHelper.TAG,
//                "Found existing fragment, not showing rationale."
//            )
//            return
//        }
//        RationaleDialogFragmentCompat
//            .newInstance(rationale, positiveButton, negativeButton, theme, requestCode, perms)
//            .showAllowingStateLoss(fm, RationaleDialogFragmentCompat.TAG)
    }
}
