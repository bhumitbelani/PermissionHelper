package com.master.permissionhelper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

class PermissionHelper {

    private val TAG = "PermissionHelperJava"
    private var REQUEST_CODE: Int = 0

    private var activity: Activity
    private var fragment: Fragment? = null
    private var permissions: Array<String> = arrayOf()
    private var mPermissionCallback: PermissionCallback? = null
    private var showRational: Boolean = false

    //=========Constructors - START=========
    constructor(activity: Activity, fragment: Fragment, permissions: Array<String>, requestCode: Int = 100) {
        this.activity = activity
        this.fragment = fragment
        this.permissions = permissions
        this.REQUEST_CODE = requestCode
        checkIfPermissionPresentInAndroidManifest()
    }

    constructor(activity: Activity, permissions: Array<String>, requestCode: Int = 100) {
        this.activity = activity
        this.permissions = permissions
        this.REQUEST_CODE = requestCode
        checkIfPermissionPresentInAndroidManifest()
    }

    private fun checkIfPermissionPresentInAndroidManifest() {
        for (permission in permissions) {
            if (!hasPermissionInManifest(permission)) {
                throw RuntimeException("Permission ($permission) Not Declared in manifest")
            }
        }
    }


    //=========Constructors- END=========
    fun request(permissionCallback: PermissionCallback?) {
        this.mPermissionCallback = permissionCallback
        if (!hasPermssion()) {
            showRational = shouldShowRational(permissions)
            ActivityCompat.requestPermissions(activity, filterNotGrantedPermission(permissions), REQUEST_CODE)
        } else {
            Log.i(TAG, "PERMISSION: Permission Granted")
            mPermissionCallback?.onPermissionGranted()
            requestAllCallback()
        }
    }

    private var requestAllCallback: () -> Unit? = fun() {}
    private var deniedCallback: (isSystem: Boolean) -> Unit? = fun(isSystem: Boolean) {}
    fun requestAll(requestAllCallback: () -> Unit,deniedCallback: (isSystem: Boolean) -> Unit) {
        this.requestAllCallback = requestAllCallback
        this.deniedCallback = deniedCallback
        request(null)
    }

    private var requestIndividualCallback: (grantedPermission: Array<String>) -> Unit? = fun(grantedPermission: Array<String>) {}
    fun requestIndividual(requestIndividualCallback: (grantedPermission: Array<String>) -> Unit,deniedCallback: (isSystem: Boolean) -> Unit) {
        this.requestIndividualCallback = requestIndividualCallback
        this.deniedCallback = deniedCallback
        request(null)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == REQUEST_CODE) {
            var denied = false
            val grantedPermissions = ArrayList<String>()
            for ((i, grantResult) in grantResults.withIndex()) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    denied = true
                } else {
                    grantedPermissions.add(permissions[i])
                }
            }

            if (denied) {
                val currentShowRational = shouldShowRational(permissions)
                if (!showRational && !currentShowRational) {
                    Log.d(TAG, "PERMISSION: Permission Denied By System")
                    mPermissionCallback?.onPermissionDeniedBySystem()
                    deniedCallback(true)
                } else {
                    Log.i(TAG, "PERMISSION: Permission Denied")
                    //Checking if any single individual permission is granted then show user that permission
                    if (grantedPermissions.isNotEmpty()) {

                        mPermissionCallback?.onIndividualPermissionGranted(grantedPermissions.toTypedArray())
                        requestIndividualCallback(grantedPermissions.toTypedArray())
                    }

                    mPermissionCallback?.onPermissionDenied()
                    deniedCallback(false)

                }
            } else {
                Log.i(TAG, "PERMISSION: Permission Granted")

                mPermissionCallback?.onPermissionGranted()
                requestAllCallback()
            }
        }
    }

    //====================================
    //====================================

    interface PermissionCallback {
        fun onPermissionGranted()

        fun onIndividualPermissionGranted(grantedPermission: Array<String>)

        fun onPermissionDenied()

        fun onPermissionDeniedBySystem()
    }

    /**
     * Return list that is not granted and we need to ask for permission
     *
     * @param permissions
     * @return
     */
    private fun filterNotGrantedPermission(permissions: Array<String>): Array<String> {
        val notGrantedPermission = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermission.add(permission)
            }
        }
        return notGrantedPermission.toTypedArray()
    }

    /**
     * Check permission is there or not
     */
    fun hasPermssion(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * Check permission is there or not for group of permissions
     *
     * @param permissions
     * @return
     */
    fun checkSelfPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    /**
     * Checking if there is need to show rational for group of permissions
     *
     * @param permissions
     * @return
     */
    private fun shouldShowRational(permissions: Array<String>): Boolean {
        var currentShowRational = false
        for (permission in permissions) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                currentShowRational = true
                break
            }
        }
        return currentShowRational
    }

    //===================
    private fun hasPermissionInManifest(permission: String): Boolean {
        try {
            val context = activity
            val info = context.packageManager?.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            if (info?.requestedPermissions != null) {
                for (p in info.requestedPermissions) {
                    if (p == permission) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Open current application detail activity so user can change permission manually.
     */
    fun openAppDetailsActivity() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + activity.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        activity.startActivity(i)
    }
}