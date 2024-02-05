package com.whatever.permission

import android.app.Activity
import android.content.Context
import androidx.annotation.Size
import androidx.fragment.app.Fragment
import com.whatever.permission.helper.PermissionHelper

internal class PermissionRequest private constructor(
    val context: Context?,
    val helper: PermissionHelper<*>,
    @Size(min = 1) private vararg val perms: String,
    val requestCode: Int,
    val rationale: String = "",
    val positiveButtonText: String = "",
    val negativeButtonText: String = "",
    val theme: Int = 0,
) {
    fun getPermissions() = perms

    companion object {
        fun build(
            activity: Activity,
            requestCode: Int,
            @Size(min = 1) vararg perms: String,
        ): PermissionRequest {
            return PermissionRequest(
                activity,
                PermissionHelper.newInstance(activity),
                perms = perms,
                requestCode,
            )
        }

        fun build(
            fragment: Fragment,
            requestCode: Int,
            @Size(min = 1) vararg perms: String,
        ): PermissionRequest {
            return PermissionRequest(
                fragment.context,
                PermissionHelper.newInstance(fragment),
                perms = perms,
                requestCode,
            )
        }
    }
}
