package com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay

import android.graphics.Bitmap
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.hfjs.kotlin_mapuse.R
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.BitmapDescriptor




open class RouteOverlay(val aMap: AMap, val startPoint: LatLng, val endPoint: LatLng) {
    protected var stationMarkers: MutableList<Marker> = ArrayList()
    protected var allPolyLines: MutableList<Polyline> = ArrayList()
    protected  var startMarker: Marker? = null
    protected  var endMarker: Marker? = null
    private  var startBit: Bitmap ? = null
    private var endBit: Bitmap? = null
    private var busBit: Bitmap? = null
    private var walkBit: Bitmap? = null
    private var driveBit: Bitmap? = null
    protected var nodeIconVisible = true


    /**
     * 去掉BusRouteOverlay上所有的Marker。
     * @since V2.1.0
     */
    open fun removeFromMap() {
        startMarker!!.remove()
        endMarker!!.remove()
        for (marker in stationMarkers) {
            marker.remove()
        }
        for (line in allPolyLines) {
            line.remove()
        }
        destroyBit()
    }

    private fun destroyBit() {
        if (startBit != null) {
            startBit!!.recycle()
            startBit = null
        }
        if (endBit != null) {
            endBit!!.recycle()
            endBit = null
        }
        if (busBit != null) {
            busBit!!.recycle()
            busBit = null
        }
        if (walkBit != null) {
            walkBit!!.recycle()
            walkBit = null
        }
        if (driveBit != null) {
            driveBit!!.recycle()
            driveBit = null
        }
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected fun getStartBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_start)
    }

    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected fun getEndBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_end)
    }
    protected fun getRideBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_ride)
    }
    /**
     * 给公交Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected fun getBusBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_bus)
    }

    /**
     * 给步行Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected fun getWalkBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_man)
    }

    protected fun getDriveBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.mipmap.amap_car)
    }

    protected fun addStartAndEndMarker() {
        startMarker = aMap.addMarker(
            MarkerOptions()
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9")
        )
        // startMarker.showInfoWindow();

        endMarker = aMap.addMarker(
            MarkerOptions().position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9")
        )
        // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    /**
     * 移动镜头到当前的视角。
     * @since V2.1.0
     */
    fun zoomToSpan() {
        try {
            val bounds = getLatLngBounds()
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    protected open fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        b.include(startPoint)
        b.include(endPoint)
        for (polyline in allPolyLines) {
            for (point in polyline.points) {
                b.include(point)
            }
        }
        return b.build()
    }

    /**
     * 路段节点图标控制显示接口。
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    fun setNodeIconVisibility(visible: Boolean) {
        try {
            nodeIconVisible = visible
            if (stationMarkers.size > 0) {
                for (i in 0 until stationMarkers.size) {
                    stationMarkers[i].isVisible = visible
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }

    protected fun addStationMarker(options: MarkerOptions?) {
        if (options == null) {
            return
        }
        val marker = aMap.addMarker(options)
        if (marker != null) {
            stationMarkers.add(marker)
        }

    }

    protected fun addPolyLine(options: PolylineOptions?) {
        if (options == null) {
            return
        }
        val polyline = aMap.addPolyline(options)
        if (polyline != null) {
            allPolyLines.add(polyline)
        }
    }

    protected open fun getRouteWidth(): Float {
        return 18f
    }

    protected fun getWalkColor(): Int {
        return Color.parseColor("#6db74d")
    }

    /**
     * 自定义路线颜色。
     * return 自定义路线颜色。
     * @since V2.2.1
     */
    protected fun getBusColor(): Int {
        return Color.parseColor("#537edc")
    }

    protected fun getDriveColor(): Int {
        return Color.parseColor("#537edc")
    }
    protected fun getRideColor(): Int {
        return Color.parseColor("#537edc")
    }


    // protected int getShowRouteZoom() {
    // return 15;
    // }
}