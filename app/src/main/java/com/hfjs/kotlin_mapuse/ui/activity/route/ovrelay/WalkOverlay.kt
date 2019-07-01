package com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay

import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.WalkPath
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.LatLng
import com.amap.api.services.route.WalkStep


class WalkOverlay(aMap: AMap, start: LatLonPoint, end: LatLonPoint, private val mWalkPath: WalkPath) :
    RouteOverlay(aMap, MapUtil.convertToLatLng(start), MapUtil.convertToLatLng(end)) {
    private lateinit var mPolylineOptions: PolylineOptions
    private lateinit var mWalkDescriptor: BitmapDescriptor

    fun addToMap() {
        initOptions()
        val walkPaths = mWalkPath.steps
        mPolylineOptions.add(startPoint)
        for (i in 0 until walkPaths.size) {
            val walkStep = walkPaths[i]
            val latlng = MapUtil.convertToLatLng(walkStep.polyline[0])
            addWalkStationMarkers(walkStep, latlng)
            addWalkPolyLines(walkStep)
        }
        mPolylineOptions.add(endPoint)
        addStartAndEndMarker()
        showPolyline()
    }
    /**
     * 检查这一步的最后一点和下一步的起始点之间是否存在空隙
     */
    private fun checkDistanceToNextStep(walkStep: WalkStep, walkStep1: WalkStep) {
        val lastPoint = getLastWalkPoint(walkStep)
        val nextFirstPoint = getFirstWalkPoint(walkStep1)
        if (lastPoint != nextFirstPoint) {
            addWalkPolyLine(lastPoint, nextFirstPoint)
        }
    }

    /**
     * @param walkStep
     * @return
     */
    private fun getLastWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[walkStep.polyline.size - 1]
    }

    /**
     * @param walkStep
     * @return
     */
    private fun getFirstWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[0]
    }


    private fun addWalkPolyLine(pointFrom: LatLonPoint, pointTo: LatLonPoint) {
        addWalkPolyLine(MapUtil.convertToLatLng(pointFrom), MapUtil.convertToLatLng(pointTo))
    }

    /**
     * 添加单条步行数据
     */
    private fun addWalkPolyLine(latLngFrom: LatLng, latLngTo: LatLng) {
        mPolylineOptions.add(latLngFrom, latLngTo)
    }

    /**
     * 添加步行数据list
     * @param walkStep
     */
    private fun addWalkPolyLines(walkStep: WalkStep) {
        mPolylineOptions.addAll(MapUtil.convertArrList(walkStep.polyline))
    }

    /**
     * @param walkStep
     * @param position
     */
    private fun addWalkStationMarkers(walkStep: WalkStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + walkStep.action + "\n\u9053\u8DEF:" + walkStep.road)
                .snippet(walkStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(mWalkDescriptor)
        )
    }

    /**
     * 初始化Options  数据和属性
     */
    private fun initOptions() {
        mWalkDescriptor = getWalkBitmapDescriptor()

        mPolylineOptions = PolylineOptions()

        mPolylineOptions.color(getWalkColor()).width(getRouteWidth())
    }

    /**
     * 展示线添加线Options数据
     */
    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }
}