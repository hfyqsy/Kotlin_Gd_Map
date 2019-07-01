package com.hfjs.kotlin_mapuse.ui.activity.cover

import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.AMap
import com.hfjs.kotlin_mapuse.R


class MarkerOverlay(amap: AMap, points: List<LatLng>, centerpoint: LatLng) {
    private val pointList = ArrayList<LatLng>()
    private var aMap: AMap?= amap
    private var centerPoint: LatLng? = centerpoint
    private var centerMarker: Marker? = null
    private val mMarkers = ArrayList<Marker>()

    init {
        initPointList(points)
        initCenterMarker()
    }

    //初始化list
    private fun initPointList(points: List<LatLng>?) {
        if (points != null && points.size > 0) {
            for (point in points) {
                pointList.add(point)
            }
        }
    }

    //初始化中心点Marker
    private fun initCenterMarker() {
        this.centerMarker = aMap!!.addMarker(
            MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(
                    BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_openmap_mark)
                )
                .position(centerPoint)
                .title("中心点")
        )
        centerMarker!!.showInfoWindow()
    }

    /**
     * 设置改变中心点经纬度
     * @param centerpoint 中心点经纬度
     */
    fun setCenterPoint(centerpoint: LatLng) {
        this.centerPoint = centerpoint
        if (centerMarker == null)
            initCenterMarker()
        this.centerMarker!!.position = centerpoint
        centerMarker!!.isVisible = true
        centerMarker!!.showInfoWindow()
    }

    /**
     * 添加Marker到地图中。
     */
    fun addToMap() {
        try {
            for (i in pointList.indices) {
                val marker = aMap!!.addMarker(
                    MarkerOptions()
                        .position(pointList[i])
                        .icon(
                            BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                )
                marker.setObject(i)
                mMarkers.add(marker)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    /**
     * 去掉MarkerOverlay上所有的Marker。
     */
    fun removeFromMap() {
        for (mark in mMarkers) {
            mark.remove()
        }
        centerMarker!!.remove()
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中，且地图中心点不变。
     */
    fun zoomToSpanWithCenter() {
        if (pointList != null && pointList.size > 0) {
            if (aMap == null)
                return
            centerMarker!!.isVisible = true
            centerMarker!!.showInfoWindow()
            val bounds = getLatLngBounds(centerPoint, pointList)
            aMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    //根据中心点和自定义内容获取缩放bounds
    private fun getLatLngBounds(centerpoint: LatLng?, pointList: List<LatLng>): LatLngBounds {
        val b = LatLngBounds.builder()
        if (centerpoint != null) {
            for (i in pointList.indices) {
                val p = pointList[i]
                val p1 = LatLng(centerpoint.latitude * 2 - p.latitude, centerpoint.longitude * 2 - p.longitude)
                b.include(p)
                b.include(p1)
            }
        }
        return b.build()
    }

    /**
     * 缩放移动地图，保证所有自定义marker在可视范围中。
     */
    fun zoomToSpan() {
        if (pointList != null && pointList.size > 0) {
            if (aMap == null)
                return
            centerMarker!!.isVisible = false
            val bounds = getLatLngBounds(pointList)
            aMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private fun getLatLngBounds(pointList: List<LatLng>): LatLngBounds {
        val b = LatLngBounds.builder()
        for (i in pointList.indices) {
            val p = pointList[i]
            b.include(p)
        }
        return b.build()
    }

    /**
     * 添加一个Marker点
     * @param latLng 经纬度
     */
    fun addPoint(latLng: LatLng) {
        pointList.add(latLng)
        val marker = aMap!!.addMarker(
            MarkerOptions().position(latLng)
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
                )
        )
        marker.setObject(pointList.size - 1)
        mMarkers.add(marker)
    }
}