package com.whatever.permission.test

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.whatever.permission.interfaces.PermissionCallbacks
import com.whatever.permission.requestPermissions

class TestPermissionActivity : AppCompatActivity(), PermissionCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(this, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        L.d("全部权限授予")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        L.d("存在被拒绝的权限 $perms")
    }

}
