package com.app.permissionExt.permission

internal object RequestPlugins {

    @Volatile
    var sRequestCallback: OnRequestPermissionResultCallback? = null

    @Volatile
    var sResultCallback: OnPermissionsResultCallback? = null

    fun setOnRequestPermissionsCallback(callback: OnRequestPermissionResultCallback) {
        sRequestCallback = callback
    }

    fun setOnPermissionsResultCallback(callback: OnPermissionsResultCallback) {
        sResultCallback = callback
    }
}