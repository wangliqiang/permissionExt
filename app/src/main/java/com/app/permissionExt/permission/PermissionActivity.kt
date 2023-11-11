package com.app.permissionExt.permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import splitties.init.appCtx

class PermissionActivity : AppCompatActivity() {

    private var rationaleDialog: AlertDialog? = null

    private val settingActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            RequestPlugins.sRequestCallback?.onSettingActivityResult()
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rationale = intent.getStringExtra(KEY_RATIONALE)
        val requestCode = intent.getIntExtra(KEY_INPUT_PERMISSIONS_CODE, 1000)
        val permissions = intent.getStringArrayExtra(KEY_INPUT_PERMISSIONS)!!

        when (intent.getIntExtra(KEY_INPUT_REQUEST_TYPE, Request.TYPE_REQUEST_PERMISSION)) {
            Request.TYPE_REQUEST_PERMISSION -> showSettingDialog(permissions, rationale) {
                ActivityCompat.requestPermissions(this, permissions, requestCode)
            }
            //跳转到设置界面
            Request.TYPE_REQUEST_SETTING -> showSettingDialog(permissions, rationale) {
                openSettingActivity()
            }
            // 所有文件的管理权限
            Request.TYPE_MANAGE_ALL_FILES_ACCESS -> showSettingDialog(permissions, rationale) {
                try {
                    if (Permissions.isManageExternalStorage()) {
                        val settingIntent =
                            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        settingActivityResult.launch(settingIntent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(appCtx, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    RequestPlugins.sRequestCallback?.onError(e)
                    finish()
                }
            }
            // 通知权限
            Request.TYPE_REQUEST_NOTIFICATIONS -> showSettingDialog(permissions, rationale) {
                kotlin.runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // 用于 API 26, 即8.0（含8.0）以上可以用
                        val intent = Intent()
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        intent.putExtra(Settings.EXTRA_CHANNEL_ID, applicationInfo.uid)
                        settingActivityResult.launch(intent)
                    } else {
                        openSettingActivity()
                    }
                }
            }
        }

    }

    private fun openSettingActivity() {
        try {
            val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingIntent.data = Uri.fromParts("package", packageName, null)
            settingActivityResult.launch(settingIntent)
        } catch (e: Exception) {
            Toast.makeText(appCtx, "无法跳转至设置界面", Toast.LENGTH_SHORT).show()
            RequestPlugins.sRequestCallback?.onError(e)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        RequestPlugins.sRequestCallback?.onRequestPermissionsResult(permissions, grantResults)
        finish()
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    private fun showSettingDialog(
        permissions: Array<String>,
        rationale: CharSequence?,
        onOk: () -> Unit
    ) {
        rationaleDialog?.dismiss()
        if (rationale.isNullOrEmpty()) {
            finish()
            return
        }

        rationaleDialog = AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(rationale)
            .setPositiveButton("好的") { _, _ ->
                onOk.invoke()
            }
            .setNegativeButton("取消") { _, _ ->
                RequestPlugins.sRequestCallback?.onRequestPermissionsResult(
                    permissions,
                    IntArray(0)
                )
                finish()
            }
            .setOnCancelListener {
                RequestPlugins.sRequestCallback?.onRequestPermissionsResult(
                    permissions,
                    IntArray(0)
                )
                finish()
            }
            .show()
    }


    companion object {

        const val KEY_RATIONALE = "KEY_RATIONALE"
        const val KEY_INPUT_REQUEST_TYPE = "KEY_INPUT_REQUEST_TYPE"
        const val KEY_INPUT_PERMISSIONS_CODE = "KEY_INPUT_PERMISSIONS_CODE"
        const val KEY_INPUT_PERMISSIONS = "KEY_INPUT_PERMISSIONS"
    }
}