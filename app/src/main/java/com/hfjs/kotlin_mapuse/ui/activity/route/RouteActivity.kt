package com.hfjs.kotlin_mapuse.ui.activity.route

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.enums.PathPlanningStrategy
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay.DrivingOverlay
import com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay.RideOverlay
import com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay.WalkOverlay
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.MapToast
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_drive.driveBottomLayout
import kotlinx.android.synthetic.main.activity_drive.firstLine
import kotlinx.android.synthetic.main.activity_drive.secondLine
import kotlinx.android.synthetic.main.activity_walk_route.*
import kotlinx.android.synthetic.main.include_map_view.*
import kotlinx.android.synthetic.main.include_recycler_view.*

class RouteActivity : BaseMapActivity(R.layout.activity_walk_route), RouteSearch.OnRouteSearchListener {


    private lateinit var mRouteSearch: RouteSearch

    //    private val mStartPoint = LatLonPoint(39.996678, 116.479271)//起点，39.996678,116.479271
//    private val mEndPoint = LatLonPoint(39.997796, 116.468939)//终点，39.997796,116.468939
    private val mStartPoint = LatLonPoint(39.942295, 116.335891)//起点，116.335891,39.942295
    private val mEndPoint = LatLonPoint(39.995576, 116.481288)//终点，116.481288,39.995576
    private val mStartPoint_bus = LatLonPoint(40.818311, 111.670801)//起点，111.670801,40.818311
    private val mEndPoint_bus = LatLonPoint(44.433942, 125.184449)//终点，
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
        route_drive.setOnClickListener {
            mMapView.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            searchDriveResult(0)
        }

        route_walk.setOnClickListener {
            mMapView.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            searchDriveResult(3)
        }

        route_ride.setOnClickListener {
            mMapView.visibility = View.VISIBLE
            mRecyclerView.visibility = View.GONE
            searchDriveResult(4)
        }
        route_bus.setOnClickListener {
            mMapView.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
            searchDriveResult(1)
        }
    }

    /**
     * 搜索驾车路径
     */
    private fun searchDriveResult(routeType: Int) {
        val fromAndTo = RouteSearch.FromAndTo(mStartPoint, mEndPoint)
        when (routeType) {
            0 -> {
                val query: RouteSearch.DriveRouteQuery =
                    RouteSearch.DriveRouteQuery(fromAndTo, PathPlanningStrategy.DRIVING_DEFAULT, null, null, "")
                mRouteSearch.calculateDriveRouteAsyn(query)
            }
            1 -> {
//                val fromAndToBus = RouteSearch.FromAndTo(mStartPoint_bus, mEndPoint_bus)
                // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
                val query: RouteSearch.BusRouteQuery = RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusDefault, "北京", 0)
                mRouteSearch.calculateBusRouteAsyn(query)
            }
            3 -> {
                val query = RouteSearch.WalkRouteQuery(fromAndTo)
                mRouteSearch.calculateWalkRouteAsyn(query)
            }
            4 -> {
                val query: RouteSearch.RideRouteQuery = RouteSearch.RideRouteQuery(fromAndTo)
                mRouteSearch.calculateRideRouteAsyn(query)
            }
        }
    }

    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
        aMap.clear()
        if (p1 != 1000) {
            MapToast.showError(this, p1)
            return
        }
        if (p0 == null || p0.paths == null) {
            toast("没有查询到相关路径")
            return
        }
        setDriveResult(p0)
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
        aMap.clear()
        if (p1 != 1000) {
            MapToast.showError(this, p1)
            return
        }
        if (p0 == null || p0.paths == null) {
            toast("没有查询到相关路径")
            return
        }
        setBusResult(p0)
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
        aMap.clear()
        if (p1 != 1000) {
            MapToast.showError(this, p1)
            return
        }
        if (p0 == null || p0.paths == null) {
            toast("没有查询到相关路径")
            return
        }
        setRideResult(p0)
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
        aMap.clear()
        if (p1 != 1000) {
            MapToast.showError(this, p1)
            return
        }
        if (p0 == null || p0.paths == null) {
            toast("没有查询到相关路径")
            return
        }
        setWalkResult(p0)
    }

    private var mRouOverlay: WalkOverlay? = null
    private fun setWalkResult(result: WalkRouteResult) {
        Logger.e("setWalkResult    $result")
        val walkPath: WalkPath = result.paths[0]
        if (mRouOverlay != null) mRouOverlay!!.removeFromMap()
        mRouOverlay = WalkOverlay(aMap, result.startPos, result.targetPos, walkPath)
        mRouOverlay!!.addToMap()
        mRouOverlay!!.zoomToSpan()
        driveBottomLayout.visibility = View.VISIBLE
        firstLine.text =
            "${MapUtil.getFriendlyTimes(walkPath.duration.toInt())} ( ${MapUtil.getFriendlyLength(walkPath.distance.toInt())} )"
        secondLine.visibility = View.GONE
        driveBottomLayout.setOnClickListener {
            val intent = Intent(this, RouteDetailActivity::class.java)
            intent.putExtra("routeType", 3)
            intent.putExtra("RouteResult", result)
            startActivity(intent)
        }

    }

    private var mDrivingOverlay: DrivingOverlay? = null
    private fun setDriveResult(result: DriveRouteResult) {
        Logger.e("setDriveResult    $result")
        val walkPath: DrivePath = result.paths[0]
        if (mDrivingOverlay != null) mDrivingOverlay!!.removeFromMap()
        mDrivingOverlay = DrivingOverlay(aMap, walkPath, result.startPos, result.targetPos, null)
        mDrivingOverlay!!.addToMap()
        mDrivingOverlay!!.zoomToSpan()
        driveBottomLayout.visibility = View.VISIBLE
        firstLine.text =
            "${MapUtil.getFriendlyTimes(walkPath.duration.toInt())} ( ${MapUtil.getFriendlyLength(walkPath.distance.toInt())} )"
        secondLine.visibility = View.VISIBLE
        secondLine.text = "打车约 ${result.taxiCost}元"
        driveBottomLayout.setOnClickListener {
            val intent = Intent(this, RouteDetailActivity::class.java)
            intent.putExtra("routeType", 0)
            intent.putExtra("RouteResult", result)
            startActivity(intent)
        }
    }

    private var mRideOverlay: RideOverlay? = null
    private fun setRideResult(result: RideRouteResult) {
        Logger.e("setDriveResult    $result")
        val ridePath: RidePath = result.paths[0]
        if (mRideOverlay != null) mRideOverlay!!.removeFromMap()
        mRideOverlay = RideOverlay(aMap, result.startPos, result.targetPos, ridePath)
        mRideOverlay!!.addToMap()
        mRideOverlay!!.zoomToSpan()
        driveBottomLayout.visibility = View.VISIBLE
        firstLine.text =
            "${MapUtil.getFriendlyTimes(ridePath.duration.toInt())} ( ${MapUtil.getFriendlyLength(ridePath.distance.toInt())} )"
        secondLine.visibility = View.GONE
        driveBottomLayout.setOnClickListener {
            val intent = Intent(this, RouteDetailActivity::class.java)
            intent.putExtra("routeType", 4)
            intent.putExtra("RouteResult", result)
            startActivity(intent)
        }
    }

    private fun setBusResult(result: BusRouteResult) {
        Logger.e("数据== "+result.paths.size   +"  "+result.paths[3].steps.size)
        val adapter = BusAdapter()
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = adapter
        adapter.setNewData(result.paths)
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val intent = Intent(this, BusDetailActivity::class.java)
            intent.putExtra("RouteResult", result)
            intent.putExtra("position", position)
            startActivity(intent)
        }

    }

    private inner class BusAdapter : BaseQuickAdapter<BusPath, BaseViewHolder>(R.layout.item_route_bus) {
        override fun convert(helper: BaseViewHolder, item: BusPath?) {
            helper.setText(R.id.bus_path_title, MapUtil.getBusPathTitle(item!!))
                .setText(R.id.bus_path_desc, MapUtil.getBusPathDes(item))
        }

    }

}