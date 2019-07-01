package com.hfjs.kotlin_mapuse.base

import android.support.annotation.LayoutRes
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.hfjs.kotlin_mapuse.utils.Logger

open class BaseLocationActivity(@LayoutRes layoutId: Int) : BaseMapActivity(layoutId) {
    protected lateinit var mLocationClient: AMapLocationClient
    protected lateinit var mLocationClientOption: AMapLocationClientOption
    protected var isEnd: Boolean = true

    protected fun initLocation() {
        //声明mlocationClient对象
        mLocationClient = AMapLocationClient(this)
        //声明mLocationOption对象
        mLocationClientOption = AMapLocationClientOption()
        //设置定位模式 battery_saving 低功耗,device_sensors 仅设备,hight_accuracy 高精度
        mLocationClientOption.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
        mLocationClientOption.interval = 2000
        mLocationClient.setLocationListener(locationListener)
        mLocationClient.startLocation()
    }

    /**
     * 定位监听事件
     */
    private val locationListener = AMapLocationListener {

        if (it != null && it.errorCode == 0) {
            //定位成功之后取消定位
            if (isEnd) mLocationClient.stopLocation()
            location(it)
        } else {
            Logger.e("定位失败原因=>  " + it.errorInfo)
        }
    }

    protected open fun location(location: AMapLocation) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient.stopLocation()
    }

}