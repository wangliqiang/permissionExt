package com.app.permissionExt.permission

interface OnPermissionsDeniedCallback {
    fun onPermissionsDenied(deniedPermissions: Array<String>)
}