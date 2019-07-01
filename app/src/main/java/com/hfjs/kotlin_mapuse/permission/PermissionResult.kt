package com.hfjs.kotlin_mapuse.permission

interface PermissionResult {
    fun onGranted()

    fun onDenied()
}
