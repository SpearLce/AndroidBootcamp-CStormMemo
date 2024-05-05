package com.illidancstormrage.cstormmemo.utils.extensions

import android.Manifest
import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.illidancstormrage.utils.toast.makeToast

fun XXPermissions.requestRecordAudio(context: Context, startRecording: () -> Unit) {

    this.permission(Permission.RECORD_AUDIO)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                if (!all) {
                    "录制未正常授予".makeToast()
                    return
                }
                "获取录制权限成功".makeToast()
                //开始录制
                startRecording()
            }

            override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                if (never) {
                    "录制权限被永久拒绝授权".makeToast()
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(context, permissions);
                } else {
                    "录制权限请求失败".makeToast()
                }
            }
        })
}

fun XXPermissions.requestInternet(context: Context, netWorkBlock: () -> Unit) {
    val permissionName = "网络"
    this.permission(Manifest.permission.INTERNET)
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                if (!all) {
                    "${permissionName}权限未正常授予".makeToast()
                    return
                }
                //"获取${permissionName}权限成功".makeToast()
                netWorkBlock()
            }

            override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                if (never) {
                    "${permissionName}权限被永久拒绝授权".makeToast()
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(context, permissions);
                } else {
                    "${permissionName}权限请求失败".makeToast()
                }
            }
        })
}