package com.whatever.permission

import L
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Size
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.whatever.permission.interfaces.PermissionCallbacks
import java.lang.reflect.InvocationTargetException

/**
 * 判断是否有权限
 */
fun Context.hasPermissions(
    @Size(min = 1) vararg perms: String,
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    perms.forEach {
        if (ContextCompat.checkSelfPermission(this, it)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return false
}

fun <T : Activity> T.requestPermissions(
    host: Activity,
    requestCode: Int,
    @Size(min = 1) vararg perms: String,
) {
    requestPermissions(PermissionRequest.build(host, requestCode, perms = perms))
}

fun <T : Fragment> T.requestPermissions(
    host: Fragment,
    requestCode: Int,
    @Size(min = 1) vararg perms: String,
) {
    requestPermissions(PermissionRequest.build(host, requestCode, perms = perms))
}

private fun requestPermissions(request: PermissionRequest) {
    // 在发送请求之前检查权限
    val hasPermissions = request.context?.hasPermissions(*request.getPermissions()) ?: false
    if (hasPermissions) {
        notifyAlreadyHasPermissions(
            request.helper.mHost,
            request.requestCode,
            request.getPermissions(),
        )
        return
    }
    // 没有权限发起权限申请
//    request.helper.requestPermissions(
//        request.rationale,
//        request.positiveButtonText,
//        request.negativeButtonText,
//        request.theme,
//        request.requestCode,
//        *request.getPermissions()
//    )
    val launcher: ActivityResultLauncher<Array<String>> =
        (request.context as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            // 判断是否存在拒绝权限
            val isGrantAll = result.entries.all { it.value }
            if (isGrantAll) {
                // 取得全部权限
                notifyAlreadyHasPermissions(
                    request.helper.mHost,
                    request.requestCode,
                    request.getPermissions()
                )
            } else {
                // 存在未获得授权的权限
                val denied: MutableList<String> = ArrayList()
                for (i in request.getPermissions().indices) {
                    if (result[request.getPermissions()[i]] == true) {
                        PackageManager.PERMISSION_GRANTED
                    } else {
                        denied.add(request.getPermissions()[i])
                    }
                }
                if (denied.isNotEmpty()) {
                    if (request.helper.mHost is PermissionCallbacks) {
                        request.helper.mHost.onPermissionsDenied(request.requestCode, denied)
                    }
                }
            }

        }
    launcher.launch(request.getPermissions().asList().toTypedArray())
}

private fun notifyAlreadyHasPermissions(
    obj: Any?,
    requestCode: Int,
    perms: Array<out String>,
) {
    val grantResults = IntArray(perms.size)
    for (i in perms.indices) {
        grantResults[i] = PackageManager.PERMISSION_GRANTED
    }
    onRequestPermissionsResult(requestCode, perms, grantResults, obj)
}

private fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    vararg receivers: Any?,
) {
    // 从请求中收集已授予和已拒绝的权限。
    val granted: MutableList<String> = ArrayList()
    val denied: MutableList<String> = ArrayList()
    for (i in permissions.indices) {
        val perm = permissions[i]
        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
            granted.add(perm)
        } else {
            denied.add(perm)
        }
    }

    // 遍历所有接收器
    for (item in receivers) {
        // 报告授予的权限
        if (granted.isNotEmpty()) {
            if (item is PermissionCallbacks) {
                item.onPermissionsGranted(requestCode, granted)
            }
        }

        // 报告被拒绝的权限
        if (denied.isNotEmpty()) {
            if (item is PermissionCallbacks) {
                item.onPermissionsDenied(requestCode, denied)
            }
        }

        // 如果 100% 成功，则调用带注释的方法
        if (granted.isNotEmpty() && denied.isEmpty()) {
            runAnnotatedMethods(item, requestCode)
        }
    }
}

private fun runAnnotatedMethods(
    obj: Any?,
    requestCode: Int,
) {
    if (obj == null) {
        return
    }
    var clazz: Class<*>? = obj.javaClass
    if (isUsingAndroidAnnotations(obj)) {
        clazz = clazz!!.superclass
    }
    while (clazz != null) {
        for (method in clazz.declaredMethods) {
            method.getAnnotation(AfterPermissionGranted::class.java)?.run {
                // 检查具有匹配请求代码的带注释的方法。
                if (value == requestCode) {
                    // 方法必须是 void
                    if (method.parameterTypes.isNotEmpty()) {
                        throw RuntimeException(
                            "Cannot execute method " + method.name + " because it is non-void method and/or has input parameters.",
                        )
                    }
                    try {
                        // 如果方法私有，则使方法可访问
                        if (!method.isAccessible) {
                            method.isAccessible = true
                        }
                        method.invoke(obj)
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        clazz = clazz.superclass
    }
}

private fun isUsingAndroidAnnotations(obj: Any): Boolean {
    return if (!obj.javaClass.simpleName.endsWith("_")) {
        false
    } else {
        try {
            val clazz = Class.forName("org.androidannotations.api.view.HasViews")
            clazz.isInstance(obj)
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}
