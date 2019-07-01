package com.hfjs.kotlin_mapuse.ui.activity.navi

import android.content.Intent
import android.util.SparseArray
import android.view.View
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.model.AMapNaviPath
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.navi.view.RouteOverLay
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseNaviActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.RouteDetailActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_drive.*

class NaviRouteActivity : BaseNaviActivity(R.layout.activity_drive) {


    /**
     * 起点坐标
     */
    private val startLatLng = NaviLatLng(39.993537, 116.472875)
    /**
     * 终点坐标
     */
    private val endLatLng = NaviLatLng(39.90759, 116.392582)
    /**
     * 起点集合
     */
    private var startList = ArrayList<NaviLatLng>()
    /**
     * 途经点集合
     */
    private var wayList = ArrayList<NaviLatLng>()
    /**
     * 终点集合
     */
    private var endList = ArrayList<NaviLatLng>()
    /**
     * 保存当前算好的路线
     */
    private val routeOverlays = SparseArray<RouteOverLay>()

    override fun initView() {
        initToolbar(intent.getIntExtra("title", 0))
        startList.add(startLatLng);
        endList.add(endLatLng);
    }

    override fun initData() {
        aMap.addMarker(
            MarkerOptions()
                .position(LatLng(startLatLng.latitude, startLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start))
        )
        aMap.addMarker(
            MarkerOptions()
                .position(LatLng(endLatLng.latitude, endLatLng.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end))
        )
    }

    override fun setListener() {
        tvDriveRoute.setOnClickListener { searchResult() }

        aMap.setOnMapClickListener {

        }
        aMap.setOnMarkerClickListener {
            false
        }

    }


    /**
     * 搜索驾车路径
     */
    private fun searchResult() {

        val strategyFlag = mAMapNavi.strategyConvert(
            true, false, false, true, false
        )
        mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag)
    }

    override fun onCalculateRouteSuccess(p0: IntArray?) {
        aMap.clear()
        cleanRouteOverlay()
        val paths = mAMapNavi.naviPaths
//        for (i in 0 until p0!!.size) {
//            val path = paths[p0[i]]
//            if (path != null) drawRoutes(p0[i], path)
//        }
//        val path = paths[p0[0]]
//        setLayoutVisibility(path)
        val path:AMapNaviPath = paths[p0!![0]]!!
        drawRoutes(p0[0], path)
        setLayoutVisibility(path)
    }

    /**
     * 设置路径
     */
    private fun drawRoutes(routeId: Int, path: AMapNaviPath) {
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0f))
        val routeOverly = RouteOverLay(aMap, path, this)
        routeOverly.width = 60f
        routeOverly.isTrafficLine = true
        routeOverly.addToMap()
        routeOverly.zoomToSpan()
        mAMapNavi.selectRouteId(routeId)
        routeOverlays.put(routeId, routeOverly)
    }

    /**
     * 设置是否显示
     */
    private fun setLayoutVisibility(path: AMapNaviPath) {
        driveBottomLayout.visibility = View.VISIBLE
        firstLine.text = path.labels
        secondLine.visibility = View.VISIBLE
        secondLine.text =MapUtil.getFriendlyTime(path.allTime)
        driveBottomLayout.setOnClickListener {
            val intent = Intent(this, NaviDetailActivity::class.java)
            intent.putExtra("gps", true)
            intent.putExtra("title", path.labels)
            startActivity(intent)
        }
    }

    /**
     * 清除路径
     */
    private fun cleanRouteOverlay() {
        for (i in 0 until routeOverlays.size()) {
            val key = routeOverlays.keyAt(i)
            val overlay = routeOverlays.get(key)
            overlay.removeFromMap()
            overlay.destroy()
        }
        routeOverlays.clear()
    }
}