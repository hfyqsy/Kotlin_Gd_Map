package com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay

import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.DrivePath
import com.amap.api.services.route.DriveStep
import com.amap.api.services.route.TMC
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.MapUtil


class DrivingOverlay(
    aMap: AMap, val drivePath: DrivePath,
    start: LatLonPoint, end: LatLonPoint, val mPointList: List<LatLonPoint>?
) : RouteOverlay(aMap, MapUtil.convertToLatLng(start), MapUtil.convertToLatLng(end)) {

    private val throughPointMarkerList = ArrayList<Marker>()
    private var throughPointMarkerVisible = true
    private var tmcs: MutableList<TMC> = ArrayList()
    private var mPolylineOptions: PolylineOptions? = null
    private var mPolylineOptionsColor: PolylineOptions? = null
    private var isColorFullLine = true
    private var mWidth = 25f
    private var mLatLngOfPath: MutableList<LatLng>? = null

    fun setIsColorFullLine(isColorFullLine: Boolean) {
        this.isColorFullLine = isColorFullLine
    }

    /**
     * 根据给定的参数，构造一个导航路线图层类对象。
     *
     * @param amap      地图对象。
     * @param path 导航路线规划方案。
     * @param context   当前的activity对象。
     */
    init {
        initBitmapDescriptor()
    }


    public override fun getRouteWidth(): Float {
        return mWidth
    }

    /**
     * 设置路线宽度
     *
     * @param mWidth 路线宽度，取值范围：大于0
     */
    fun setRouteWidth(mWidth: Float) {
        this.mWidth = mWidth
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    fun addToMap() {
        initPolylineOptions()
        try {
            if (mWidth == 0f) return
            mLatLngOfPath = ArrayList()
            val drivePaths = drivePath.steps
            mPolylineOptions!!.add(startPoint)
            for (i in drivePaths.indices) {
                val step = drivePaths[i]
                val latLonPoints = step.polyline
                val tmcList = step.tmCs
                tmcs.addAll(tmcList)
                addDrivingStationMarkers(step, convertToLatLng(latLonPoints[0]))
                for (latLonPoint:LatLonPoint in latLonPoints) {
                    mPolylineOptions!!.add(convertToLatLng(latLonPoint))
                    mLatLngOfPath!!.add(convertToLatLng(latLonPoint))
                }
            }
            mPolylineOptions!!.add(endPoint)
            if (startMarker != null) {
                startMarker!!.remove()
                startMarker = null
            }
            if (endMarker != null) {
                endMarker!!.remove()
                endMarker = null
            }
            addStartAndEndMarker()
            addThroughPointMarker()
            if (isColorFullLine && tmcs.size > 0) {
                colorWayUpdate(tmcs)
                showColorPolyline()
            } else {
                showPolyline()
            }

        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * 初始化线段属性
     */
    private fun initPolylineOptions() {
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions!!.color(getDriveColor()).width(getRouteWidth())
    }

    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }

    private fun showColorPolyline() {
        addPolyLine(mPolylineOptionsColor)

    }

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     *
     * @param tmcSection
     */
    private fun colorWayUpdate(tmcSection: List<TMC>?) {
        if (tmcSection == null || tmcSection.isEmpty()) {
            return
        }
        var segmentTrafficStatus: TMC
        mPolylineOptionsColor = null
        mPolylineOptionsColor = PolylineOptions()
        mPolylineOptionsColor!!.width(getRouteWidth())
        val colorList = ArrayList<Int>()
        val bitmapDescriptors: MutableList<BitmapDescriptor> = ArrayList()
        val points = ArrayList<LatLng>()
        val texIndexList = ArrayList<Int>()
        //        mPolylineOptionscolor.add(startPoint);
        //        mPolylineOptionscolor.add(MapUtil.convertToLatLng(tmcSection.get(0).getPolyline().get(0)));

        points.add(startPoint)
        points.add(MapUtil.convertToLatLng(tmcSection[0].polyline[0]))
        colorList.add(getDriveColor())
        bitmapDescriptors.add(defaultRoute!!)

        var textIndex = 0
        texIndexList.add(textIndex)
        texIndexList.add(++textIndex)
        for (i in tmcSection.indices) {
            segmentTrafficStatus = tmcSection[i]
            val color = getColor(segmentTrafficStatus.status)
            val bitmapDescriptor = getTrafficBitmapDescriptor(segmentTrafficStatus.status)
            val polyline = segmentTrafficStatus.polyline
            for (j in polyline.indices) {
                points.add(MapUtil.convertToLatLng(polyline[j]))
                colorList.add(color)
                texIndexList.add(++textIndex)
                bitmapDescriptors.add(bitmapDescriptor!!)
            }
        }

        points.add(endPoint)
        colorList.add(getDriveColor())
        bitmapDescriptors.add(defaultRoute!!)
        texIndexList.add(++textIndex)
        mPolylineOptionsColor!!.addAll(points)
        mPolylineOptionsColor!!.colorValues(colorList)

    }

    private var defaultRoute: BitmapDescriptor? = null
    private var unknownTraffic: BitmapDescriptor? = null
    private var smoothTraffic: BitmapDescriptor? = null
    private var slowTraffic: BitmapDescriptor? = null
    private var jamTraffic: BitmapDescriptor? = null
    private var veryJamTraffic: BitmapDescriptor? = null
    private fun initBitmapDescriptor() {
        defaultRoute = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_6_arrow)
        smoothTraffic = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_4_arrow)
        unknownTraffic = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_0_arrow)
        slowTraffic = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_3_arrow)
        jamTraffic = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_2_arrow)
        veryJamTraffic = BitmapDescriptorFactory.fromResource(R.mipmap.amap_route_color_texture_9_arrow)

    }

    private fun getTrafficBitmapDescriptor(status: String): BitmapDescriptor? {

        Logger.e("==> 路况信息 is $status")
        if (status == "畅通") {
            return smoothTraffic
        } else if (status == "缓行") {
            return slowTraffic
        } else if (status == "拥堵") {
            return jamTraffic
        } else return if (status == "严重拥堵") {
            veryJamTraffic
        } else {
            defaultRoute
        }
    }

    private fun getColor(status: String): Int {
        if (status == "畅通") {
            return Color.GREEN
        } else if (status == "缓行") {
            return Color.YELLOW
        } else if (status == "拥堵") {
            return Color.RED
        } else return if (status == "严重拥堵") {
            Color.parseColor("#990033")
        } else {
            Color.parseColor("#537edc")
        }
    }

    fun convertToLatLng(point: LatLonPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    /**
     * @param driveStep
     * @param latLng
     */
    private fun addDrivingStationMarkers(driveStep: DriveStep, latLng: LatLng) {
        addStationMarker(
            MarkerOptions().position(latLng)
                .title("\u65B9\u5411:" + driveStep.action + "\n\u9053\u8DEF:" + driveStep.road)
                .snippet(driveStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor())
        )
    }

    override fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        b.include(startPoint)
        b.include(endPoint)
        if (mPointList != null && mPointList.isNotEmpty()) {
            for (i in mPointList.indices) {
                b.include(LatLng(mPointList[i].latitude, mPointList[i].longitude))
            }
        }
        return b.build()
    }

    fun setThroughPointIconVisibility(visible: Boolean) {
        throughPointMarkerVisible = visible
        if ((throughPointMarkerList.size > 0)) {
            for (i in throughPointMarkerList.indices) {
                throughPointMarkerList[i].isVisible = visible
            }
        }
    }

    private fun addThroughPointMarker() {
        if (mPointList != null && mPointList.isNotEmpty()) {
            var latLonPoint: LatLonPoint? = null
            for (i in mPointList.indices) {
                latLonPoint = mPointList[i]
                throughPointMarkerList.add(
                    aMap.addMarker(
                        MarkerOptions().position(LatLng(latLonPoint.latitude, latLonPoint.longitude))
                            .visible(throughPointMarkerVisible)
                            .icon(getThroughPointBitDes())
                            .title("\u9014\u7ECF\u70B9")
                    )
                )
            }
        }
    }

    private fun getThroughPointBitDes(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_through)
    }

    /**
     * 获取两点间距离
     *
     * @param start
     * @param end
     * @return
     */
    fun calculateDistance(start: LatLng, end: LatLng): Int {
        val x1 = start.longitude
        val y1 = start.latitude
        val x2 = end.longitude
        val y2 = end.latitude
        return calculateDistance(x1, y1, x2, y2)
    }

    fun calculateDistance(x1: Double, y1: Double, x2: Double, y2: Double): Int {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        val NF_pi = 0.01745329251994329 // 弧度 PI/180
        x1 *= NF_pi
        y1 *= NF_pi
        x2 *= NF_pi
        y2 *= NF_pi
        val sinx1 = Math.sin(x1)
        val siny1 = Math.sin(y1)
        val cosx1 = Math.cos(x1)
        val cosy1 = Math.cos(y1)
        val sinx2 = Math.sin(x2)
        val siny2 = Math.sin(y2)
        val cosx2 = Math.cos(x2)
        val cosy2 = Math.cos(y2)
        val v1 = DoubleArray(3)
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2
        v1[2] = siny1 - siny2
        val dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2])

        return (Math.asin(dist / 2) * 12742001.5798544).toInt()
    }


    //获取指定两点之间固定距离点
    fun getPointForDis(sPt: LatLng, ePt: LatLng, dis: Double): LatLng {
        val lSegLength = calculateDistance(sPt, ePt).toDouble()
        val preResult = dis / lSegLength
        return LatLng(
            (ePt.latitude - sPt.latitude) * preResult + sPt.latitude,
            (ePt.longitude - sPt.longitude) * preResult + sPt.longitude
        )
    }

    /**
     * 去掉DriveLineOverlay上的线段和标记。
     */
    override fun removeFromMap() {
        try {
            super.removeFromMap()
            if ((throughPointMarkerList.size > 0)) {
                for (i in throughPointMarkerList.indices) {
                    throughPointMarkerList[i].remove()
                }
                throughPointMarkerList.clear()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}