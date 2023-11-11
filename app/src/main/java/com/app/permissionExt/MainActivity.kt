package com.app.permissionExt

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.permissionExt.permission.Permissions
import com.app.permissionExt.permission.PermissionsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storageBtn = findViewById<Button>(R.id.storage_btn)
        val notificationBtn = findViewById<TextView>(R.id.notification_btn)
        val contractBtn = findViewById<TextView>(R.id.contract_btn)

        storageBtn.setOnClickListener {
            PermissionsCompat.Builder()
                .addPermissions(*Permissions.Group.STORAGE)
                .rationale("访问存储卡权限，请前往“设置”—“应用权限”，打开所需权限")
                .onGranted {
                    Toast.makeText(this, "存储权限获取成功", Toast.LENGTH_SHORT).show()
                }
                .onDenied {
                    println("Denied=$it")
                }
                .onError {
                    println("Error=$it")
                }
                .request()
        }
        notificationBtn.setOnClickListener {
            PermissionsCompat.Builder()
                .addPermissions(Permissions.POST_NOTIFICATIONS)
                .rationale("需要发送通知来显示信息")
                .onGranted {
                    Toast.makeText(this, "通知权限获取成功", Toast.LENGTH_SHORT).show()
                }.request()
        }

        contractBtn.setOnClickListener {
            PermissionsCompat.Builder()
                .addPermissions(*Permissions.Group.CONTACTS)
                .rationale("需要通讯录权限")
                .onGranted {
                    Toast.makeText(this, "通讯录权限获取成功", Toast.LENGTH_SHORT).show()
                }.request()
        }
    }
}