package com.example.demo

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import com.master.permissionhelper.PermissionHelper

class MainActivityKt : AppCompatActivity() {
    private val TAG = "MainActivity"

    private val permissionHelper by lazy {
        PermissionHelper(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionHelper.requestIndividual(
            requestIndividualCallback = {
                Log.d(TAG, "Individual Permission Granted")
            }, deniedCallback = {isDeniedBySystem->
                if (isDeniedBySystem) {
                    Log.d(TAG, "onPermissionDeniedBySystem() called")
                    permissionHelper.openAppDetailsActivity()
                } else {
                    Log.d(TAG, "onPermissionDenied() called")
                }
            })

        permissionHelper.requestAll(
            requestAllCallback = {
                Log.d(TAG, "All Permission Granted")
            },
            deniedCallback = {isDeniedBySystem->
                if (isDeniedBySystem) {
                    Log.d(TAG, "onPermissionDeniedBySystem() called")
                    permissionHelper.openAppDetailsActivity()
                } else {
                    Log.d(TAG, "onPermissionDenied() called")
                }
            })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}