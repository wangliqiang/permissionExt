package com.app.permissionExt.permission

interface OnRequestPermissionResultCallback {

    fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray)

    fun onSettingActivityResult()

    fun onError(e: Exception)

}