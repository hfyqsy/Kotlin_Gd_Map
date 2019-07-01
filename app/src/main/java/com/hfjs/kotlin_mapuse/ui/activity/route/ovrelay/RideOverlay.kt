package com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay

import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RidePath
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.LatLng
import com.amap.api.services.route.RideStep


class RideOverlay(aMap: AMap, start: LatLonPoint, end: LatLonPoint, private val mRidePath: RidePath) :
    RouteOverlay(aMap, MapUtil.convertToLatLng(start), MapUtil.convertToLatLng(end)) {
    private lateinit var mPolylineOptions: PolylineOptions
    private lateinit var mDescriptor: BitmapDescriptor


    /**
     * 添加骑行路线到地图中。
     * @since V3.5.0
     */
    fun addToMap() {
        initOptions()
        val ridePaths = mRidePath.steps
        mPolylineOptions.add(startPoint)
        for (i in ridePaths.indices) {
            val rideStep = ridePaths.get(i)
            val latLng = MapUtil.convertToLatLng(rideStep.polyline.get(0))
            addRideStationMarkers(rideStep, latLng)
            addRidePolyLines(rideStep)
        }
        mPolylineOptions.add(endPoint)
        addStartAndEndMarker()
        showPolyline()
    }


    /**
     * @param rideStep
     */
    private fun addRidePolyLines(rideStep: RideStep) {
        mPolylineOptions.addAll(MapUtil.convertArrList(rideStep.polyline))
    }

    /**
     * @param rideStep
     * @param position
     */
    private fun addRideStationMarkers(rideStep: RideStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + rideStep.action + "\n\u9053\u8DEF:" + rideStep.road)
                .snippet(rideStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(mDescriptor)
        )
    }

    /**
     * 初始化Options  数据和属性
     */
    private fun initOptions() {
        mDescriptor = getRideBitmapDescriptor()

        mPolylineOptions = PolylineOptions()

        mPolylineOptions.color(getRideColor()).width(getRouteWidth())
    }

    /**
     * 展示线添加线Options数据
     */
    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }
}