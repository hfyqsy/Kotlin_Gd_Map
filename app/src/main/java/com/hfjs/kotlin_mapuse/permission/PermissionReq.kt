package com.hfjs.kotlin_mapuse.permission

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.SparseArray

import java.util.ArrayList

/**
 * Android运行时权限申请
 *
 *
 * 需要申请的权限列表，<a></a>"href=https://developer.android.google.cn/guide/topics/security/permissions.html?hl=zh-cn#normal-dangerous">Google Doc
 *
 *
 * -CALENDAR<br></br>
 * [android.Manifest.permission.READ_CALENDAR]<br></br>
 * [android.Manifest.permission.WRITE_CALENDAR]<br></br>
 *
 *
 * -CAMERA<br></br>
 * [android.Manifest.permission.CAMERA]<br></br>
 *
 *
 * -CONTACTS<br></br>
 * [android.Manifest.permission.READ_CONTACTS]<br></br>
 * [android.Manifest.permission.WRITE_CONTACTS]<br></br>
 * [android.Manifest.permission.GET_ACCOUNTS]<br></br>
 *
 *
 * -LOCATION<br></br>
 * [android.Manifest.permission.ACCESS_FINE_LOCATION]<br></br>
 * [android.Manifest.permission.ACCESS_COARSE_LOCATION]<br></br>
 *
 *
 * -MICROPHONE<br></br>
 * [android.Manifest.permission.RECORD_AUDIO]<br></br>
 *
 *
 * -PHONE<br></br>
 * [android.Manifest.permission.READ_PHONE_STATE]<br></br>
 * [android.Manifest.permission.CALL_PHONE]<br></br>
 * [android.Manifest.permission.READ_CALL_LOG]<br></br>
 * [android.Manifest.permission.WRITE_CALL_LOG]<br></br>
 * [android.Manifest.permission.ADD_VOICEMAIL]<br></br>
 * [android.Manifest.permission.USE_SIP]<br></br>
 * [android.Manifest.permission.PROCESS_OUTGOING_CALLS]<br></br>
 *
 *
 * -SENSORS<br></br>
 * [android.Manifest.permission.BODY_SENSORS]<br></br>
 *
 *
 * -SMS<br></br>
 * [android.Manifest.permission.SEND_SMS]<br></br>
 * [android.Manifest.permission.RECEIVE_SMS]<br></br>
 * [android.Manifest.permission.READ_SMS]<br></br>
 * [android.Manifest.permission.RECEIVE_WAP_PUSH]<br></br>
 * [android.Manifest.permission.RECEIVE_MMS]<br></br>
 *
 *
 * -STORAGE<br></br>
 * [android.Manifest.permission.READ_EXTERNAL_STORAGE]<br></br>
 * [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]<br></br>
 */
class PermissionReq private constructor(private val mObject: Any) {
    private lateinit var mPermissions: Array<String>
    private var mResult: PermissionResult? = null

    fun permissions(permissions: Array<String>): PermissionReq {
        mPermissions = permissions
        return this
    }

    fun result(result: PermissionResult?): PermissionReq {
        mResult = result
        return this
    }

    fun request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mResult != null) {
                mResult!!.onGranted()
            }
            return
        }

        val activity =
            getActivity(mObject) ?: throw IllegalArgumentException(mObject.javaClass.name + " is not supported")

        val deniedPermissionList = getDeniedPermissions(activity, mPermissions!!)
        if (deniedPermissionList.isEmpty()) {
            if (mResult != null) {
                mResult!!.onGranted()
            }
            return
        }

        val requestCode = genRequestCode()
        val deniedPermissions = deniedPermissionList.toTypedArray()
        requestPermissions(mObject, deniedPermissions, requestCode)
        sResultArray.put(requestCode, mResult)
    }

    companion object {
        private var sRequestCode = 0
        private val sResultArray = SparseArray<PermissionResult>()

        fun with(activity: Activity): PermissionReq {
            return PermissionReq(activity)
        }

        fun with(fragment: Fragment): PermissionReq {
            return PermissionReq(fragment)
        }

        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            val result = sResultArray.get(requestCode) ?: return

            sResultArray.remove(requestCode)

            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    result.onDenied()
                    return
                }
            }
            result.onGranted()
        }

        @TargetApi(Build.VERSION_CODES.M)
        private fun requestPermissions(`object`: Any, permissions: Array<String>, requestCode: Int) {
            if (`object` is Activity) {
                `object`.requestPermissions(permissions, requestCode)
            } else if (`object` is Fragment) {
                `object`.requestPermissions(permissions, requestCode)
            }
        }

        private fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> {
            val deniedPermissionList = ArrayList<String>()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permission)
                }
            }
            return deniedPermissionList
        }

        private fun getActivity(any: Any?): Activity? {
            if (any != null) {
                if (any is Activity) {
                    return any
                } else if (any is Fragment) {
                    return any.activity
                }
            }
            return null
        }

        private fun genRequestCode(): Int {
            return ++sRequestCode
        }
    }
}
