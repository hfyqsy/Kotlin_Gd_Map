package com.hfjs.kotlin_mapuse.ui.activity.route

import android.content.Intent
import android.view.View
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay.DrivingOverlay
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_drive.*


class DriveActivity : BaseMapActivity(R.layout.activity_drive), RouteSearch.OnRouteSearchListener {


    private lateinit var mDriveResult: DriveRouteResult
    private lateinit var mRouteSearch: RouteSearch
    private val mStartPoint = LatLonPoint(39.942295, 116.335891)//起点，116.335891,39.942295
    private val mEndPoint = LatLonPoint(39.995576, 116.481288)//终点，116.481288,39.995576
    //    private val mStartPoint_bus = LatLonPoint(40.818311, 111.670801)//起点，111.670801,40.818311
//    private val mEndPoint_bus = LatLonPoint(44.433942, 125.184449)//终点，
    override fun initView() {
        initToolbar(intent.getIntExtra("title", 0))
        mRouteSearch = RouteSearch(this)
        mRouteSearch.setRouteSearchListener(this)
    }

    override fun initData() {
        aMap.addMarker(
            MarkerOptions()
                .position(LatLng(mStartPoint.latitude, mStartPoint.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start))
        )
        aMap.addMarker(
            MarkerOptions()
                .position(LatLng(mEndPoint.latitude, mEndPoint.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end))
        )
    }

    override fun setListener() {
        tvDriveRoute.setOnClickListener { searchResult(RouteSearch.DRIVING_SINGLE_DEFAULT) }

        aMap.setOnMapClickListener {

        }
        aMap.setOnMarkerClickListener {
            false
        }

    }


    /**
     * 搜索驾车路径
     */
    private fun searchResult(mode: Int) {
        val fromAndTo = RouteSearch.FromAndTo(mStartPoint, mEndPoint)
        val query: RouteSearch.DriveRouteQuery = RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "")
        mRouteSearch.calculateDriveRouteAsyn(query)
    }


    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        aMap.clear()
        if (p1 != 1000) return
        if (p0 == null || p0.paths == null) {
            toast("没有查询到相关路径")
        } else {

            mDriveResult = p0
            val drivePath = mDriveResult.paths[0]
            val driveOverlay = DrivingOverlay(
                aMap,
                drivePath,
                mDriveResult.startPos,
                mDriveResult.targetPos,
                null
            )
            //是否用颜色展示交通拥堵情况，默认true
            driveOverlay.setNodeIconVisibility(false)
            //是否用颜色展示交通拥堵情况，默认true
            driveOverlay.setIsColorFullLine(true)
            driveOverlay.removeFromMap()
            driveOverlay.addToMap()
            driveOverlay.zoomToSpan()
            driveBottomLayout.visibility = View.VISIBLE
            firstLine.text =
                "${MapUtil.getFriendlyTime(drivePath.duration.toInt())} ( ${MapUtil.getFriendlyLength(drivePath.distance.toInt())} )"
            secondLine.visibility = View.VISIBLE
            secondLine.text = "打车约 ${mDriveResult.taxiCost}元"
            driveBottomLayout.setOnClickListener {
                val intent = Intent(this, RouteDetailActivity::class.java)
                intent.putExtra("RouteResult", mDriveResult)
                startActivity(intent)
            }

        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {

    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {

    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {

    }

}