package com.whatever.permission.test

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.whatever.permission.requestPermissions

class TestPermissionFragment : Fragment() {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissions(this, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
