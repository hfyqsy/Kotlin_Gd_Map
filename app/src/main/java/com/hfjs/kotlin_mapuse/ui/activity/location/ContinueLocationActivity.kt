package com.hfjs.kotlin_mapuse.ui.activity.location

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.model.*
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseLocationActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_continue.*
import java.util.*

class ContinueLocationActivity : BaseLocationActivity(R.layout.activity_continue) {
    private var count = 0
    private var mCircle: Circle? = null
    private var mLocMarker: Marker? = null
    private var mSensorHelper: SensorEventHelper? = null
    private var showCircle: Boolean = false
    private var isWifiOpen: Boolean = false
    private var mTimerTask: TimerTask? = null
    private val mTimer = Timer()
    private var mStartTime: Int = 0
    private val interpolator = CycleInterpolator(1f)
    private val interpolator1 = LinearInterpolator()

    override fun initView() {
        //初始化定位
        initLocation()
        setMapState()

        mSensorHelper = SensorEventHelper(this)
        mSensorHelper!!.registerSensorListener()
    }

    /**
     * 设置定位属性
     */
    private fun setMapState() {
        // 设置默认定位按钮是否显示
//        aMap.uiSettings.isMyLocationButtonEnabled = true
//        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.isMyLocationEnabled = true
//        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        val locationStyle = MyLocationStyle()
//        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
//        aMap.myLocationStyle = locationStyle
        aMap.uiSettings.isMyLocationButtonEnabled=true
    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        //单次定位
        tvWord.setOnClickListener {
            isEnd = true
            showCircle = false
            isWifiOpen = false
            mLocationClientOption.isNeedAddress = true
            mLocationClientOption.isOnceLocation = true
            mLocationClientOption.isLocationCacheEnable = false
            mLocationClient.stopLocation()
            mLocationClient.startLocation()
            mTimer.cancel()
        }
        //连续定位
        tvContinue.setOnClickListener {
            count = 0
            isEnd = false
            showCircle = false
            isWifiOpen = false
            mLocationClientOption.isNeedAddress = true
            mLocationClientOption.isOnceLocation = false
            mLocationClientOption.isLocationCacheEnable = true
            mLocationClient.stopLocation()
            mLocationClient.startLocation()

            mTimer.cancel()
        }
        //方向定位显示
        tvRotation.setOnClickListener {
            count = 0
            isEnd = false
            showCircle = true
            isWifiOpen = false
            mLocationClientOption.isNeedAddress = true
            mLocationClientOption.isOnceLocation = false
            mLocationClientOption.isLocationCacheEnable = true
            mLocationClient.stopLocation()
            mLocationClient.startLocation()
        }

        tvExact.setOnClickListener {
            showCircle = false
            isWifiOpen = true
            showDialog()
        }
    }


    /**
     * 定位
     */
    override fun location(location: AMapLocation) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (!isEnd) {
            count++
            if (showCircle) {
                if (mCircle == null) {
                    addMarker(latLng)
                    addCircle(latLng, location.accuracy.toDouble())
                }
                mCircle!!.center = latLng
                mCircle!!.radius = location.accuracy.toDouble()
                scaleCircle(mCircle!!)
            }
        } else {
            count = 1
            addMarker(latLng)
            if (showCircle) {
                addCircle(latLng, location.accuracy.toDouble())
            }
        }
        MapUtil.moveMap(aMap, latLng)
        val callBackTime = System.currentTimeMillis()
        val sb = StringBuffer()
        sb.append("定位完成 $count  次\n")
        sb.append("回调时间: " + MapUtil.formatUTC(callBackTime, null) + "\n")
        sb.append(MapUtil.getLocationStr(location))
        tvLocationDesc.text = sb.toString()

    }

    /**
     * 添加Marker的定位图标
     */
    private fun addMarker(latLng: LatLng) {
        if (mLocMarker != null) {
            return
        }
        val options = MarkerOptions()
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked))
        options.anchor(0.5f, 0.5f)
        options.position(latLng)
        mLocMarker = aMap.addMarker(options)
        mLocMarker!!.title = "location"
        mSensorHelper!!.setCurrentMarker(mLocMarker)
    }

    /**
     * 设置精度圈属性
     */
    private fun addCircle(latLng: LatLng, radius: Double) {
        val options = CircleOptions()
        options.strokeWidth(1f)
        options.fillColor(Color.argb(10, 0, 0, 180))
        options.strokeColor(Color.argb(180, 3, 145, 255))
        options.center(latLng)
        options.radius(radius)
        mCircle = aMap.addCircle(options)
        scaleCircle(mCircle!!)
    }

    private fun scaleCircle(circle: Circle) {
        mStartTime = SystemClock.uptimeMillis().toInt()
        mTimerTask = CircleTask(circle, 2000)
        mTimer.schedule(mTimerTask, 0, 30)
    }

    private var mAlertDialog: AlertDialog? = null
    /**
     * 开启WIFI弹窗
     */
    private fun showDialog() {
        if (mAlertDialog == null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this, R.style.DialogTransparent)
            val view=View.inflate(this,R.layout.dialog_message,null)
            builder.setView(view)
            view.findViewById<TextView>(R.id.tv_message).setText(R.string.dialog_message_location)

            val positiveButton=view.findViewById<TextView>(R.id.positiveButton)
            positiveButton.visibility=View.VISIBLE
            positiveButton .setText(R.string.desc_cancle)
            positiveButton.setOnClickListener {
                mAlertDialog!!.dismiss()

            }

           val negativeButton= view.findViewById<TextView>(R.id.negativeButton)
            negativeButton.setText(R.string.desc_open_wifi)
            negativeButton.setOnClickListener {
                mAlertDialog!!.dismiss()
                startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
            }

            mAlertDialog = builder.create()
            mAlertDialog!!.show()
        } else {
            mAlertDialog!!.show()
        }
    }

    private inner class CircleTask(private val circle: Circle, rate: Long) : TimerTask() {
        private val r: Double = circle.radius
        private var duration: Long = 1000

        init {
            if (rate > 0) {
                duration = rate
            }
        }

        override fun run() {
            val elapsed = SystemClock.uptimeMillis() - mStartTime
            val input = elapsed.toFloat() / duration
            //外圈循环缩放
            //float t = interpolator.getInterpolation((float)(input-0.25));//return (float)(Math.sin(2 * mCycles * Math.PI * input))
            //double r1 = (t + 2) * r;
            //外圈放大后消失
            val t = interpolator1.getInterpolation(input)
            val r1 = (t + 1) * r
            circle.radius = r1
            if (input > 2) {
                mStartTime = SystemClock.uptimeMillis().toInt()
            }
        }
    }

    /**
     *精度圈事件监听
     */
    private inner class SensorEventHelper(val context: Context) : SensorEventListener {
        //传感器管理类
        private var mSensorManager: SensorManager? = null
        //传感器
        private var mSensor: Sensor? = null
        //Marker
        private var mMarker: Marker? = null
        private var lastTime: Long = 0
        private var mAngle: Float = 0f

        init {
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        }

        /**
         * 注册方向传感器监听
         */
        fun registerSensorListener() {
            mSensorManager!!.registerListener(this, mSensor!!, SensorManager.SENSOR_DELAY_NORMAL)
        }

        /**
         * 解除方向传感器监听
         */
        fun unregisterSensorListener() {
            mSensorManager!!.unregisterListener(this, mSensor!!)
        }

        /**
         * 设置当前Marker
         */
        fun setCurrentMarker(marker: Marker?) {
            mMarker = marker
        }


        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        /**
         * 传感器变化结束回调
         */
        override fun onSensorChanged(event: SensorEvent) {
            if (System.currentTimeMillis() - lastTime < 1000) {
                return
            }
            when (event.sensor.type) {
                Sensor.TYPE_ORIENTATION -> {
                    var x = event.values[0]
                    x += getScreenRotationOnPhone(context)
                    x %= 360.0f
                    if (x > 180.0f) x -= 360.0f
                    else if (x < -180.0f) x += 360.0f

                    if (Math.abs(mAngle - x) >= 3.0f) {
                        mAngle = if (java.lang.Float.isNaN(x)) 0f else x
                        if (mMarker != null) {
                            mMarker!!.rotateAngle = 360 - mAngle
                        }
                        lastTime = System.currentTimeMillis()
                    }
                }
            }
        }

        /**
         * 获取当前屏幕旋转角度
         *
         * @param context
         * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
         */
        fun getScreenRotationOnPhone(context: Context): Int {
            val display = (context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

            when (display.rotation) {
                Surface.ROTATION_0 -> return 0
                Surface.ROTATION_90 -> return 90
                Surface.ROTATION_180 -> return 180
                Surface.ROTATION_270 -> return -90
            }
            return 0
        }
    }

    override fun onResume() {
        super.onResume()
        if (mSensorHelper != null) mSensorHelper!!.registerSensorListener()
        if (isWifiOpen) {
            isEnd = true
            showCircle = false
            isWifiOpen = false
            mLocationClientOption.isNeedAddress = true
            mLocationClientOption.isOnceLocation = true
            mLocationClientOption.isSensorEnable = false
            mLocationClientOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
            mLocationClient.stopLocation()
            mLocationClient.startLocation()
            mTimer.cancel()
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorHelper!!.unregisterSensorListener()
        mSensorHelper!!.setCurrentMarker(null)
        mSensorHelper = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer.cancel()
        mLocMarker!!.destroy()
        mLocMarker = null
    }

}