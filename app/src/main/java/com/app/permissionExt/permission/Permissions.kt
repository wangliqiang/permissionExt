package com.app.permissionExt.permission

import android.os.Build

object Permissions {
    const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

    const val CAMERA = "android.permission.CAMERA"

    const val READ_CONTACTS = "android.permission.READ_CONTACTS"
    const val WRITE_CONTACTS = "android.permission.WRITE_CONTACTS"
    const val GET_ACCOUNTS = "android.permission.GET_ACCOUNTS"

    const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    const val WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE"
    const val MANAGE_EXTERNAL_STORAGE = "android.permission.MANAGE_EXTERNAL_STORAGE"



    object Group {
        val STORAGE = if (isManageExternalStorage()) {
            arrayOf(MANAGE_EXTERNAL_STORAGE)
        } else {
            arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
        }

        val CAMERA = arrayOf(Permissions.CAMERA)

        val CONTACTS = arrayOf(READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS)
    }

    fun isManageExternalStorage(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}