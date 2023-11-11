package com.app.permissionExt.permission

import android.os.Handler
import android.os.Looper
import java.util.Collections
import java.util.Stack

internal object RequestManager : OnPermissionsResultCallback {

    private var requestStack: Stack<Request>? = null
    private var request: Request? = null

    private val handler = Handler(Looper.getMainLooper())

    private val requestRunnable = Runnable {
        request?.start()
    }

    private val isCurrentRequestInvalid: Boolean
        get() = request?.let { System.currentTimeMillis() - it.requestTime > 5 * 1000L } ?: true

    init {
        RequestPlugins.setOnPermissionsResultCallback(this)
    }

    fun pushRequest(request: Request?) {
        if (request == null) return

        if (requestStack == null) {
            requestStack = Stack()
        }

        requestStack?.let {
            val index = it.indexOf(request)
            if (index > 0) {
                val to = it.size - 1
                if (index != to) {
                    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                    Collections.swap(requestStack, index, to)
                }
            } else {
                it.push(request)
            }
            if (!it.empty() && isCurrentRequestInvalid) {
                RequestManager.request = it.pop()
                handler.post(requestRunnable)
            }
        }
    }

    private fun startNextRequest() {
        request?.clear()
        request = null

        requestStack?.let {
            request = if (it.empty()) null else it.pop()
            request?.let { handler.post(requestRunnable) }
        }
    }


    override fun onPermissionsGranted() {
        startNextRequest()
    }

    override fun onPermissionsDenied(deniedPermissions: Array<String>?) {
        startNextRequest()
    }

    override fun onError(e: Exception) {
        startNextRequest()
    }
}