package com.hfjs.kotlin_mapuse.ui.activity.route

import android.support.v7.widget.LinearLayoutManager
import com.amap.api.services.route.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_drive_route.*
import kotlinx.android.synthetic.main.include_recycler_view.*

class RouteDetailActivity : BaseActivity(R.layout.activity_drive_route) {

    private  var size:Int=0
    override fun initView() {
        initToolbar("路线详情")

    }

    override fun initData() {
        val routeType = intent.getIntExtra("routeType", 0)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        when (routeType) {
            0 -> {
                val mDriveRouteResult: DriveRouteResult = intent.getParcelableExtra("RouteResult")
                val mDrivePath: DrivePath = mDriveRouteResult.paths[0]
                tvFistLine.text =
                    "${MapUtil.getFriendlyTimes(mDrivePath.duration.toInt())} ( ${MapUtil.getFriendlyLength(mDrivePath.distance.toInt())} )"
                tvSecondLine.text = "打车约 ${mDriveRouteResult.taxiCost}元"

                val mAdapter = RouteTypeAdapter<DriveStep>(0)
                mRecyclerView.adapter = mAdapter
                val steps=mDrivePath.steps
                steps.add(0,DriveStep())
                steps.add(DriveStep())
                mAdapter.setNewData(steps)
                size=steps.size
            }

            3 -> {
                val mWalkRouteResult: WalkRouteResult = intent.getParcelableExtra("RouteResult")
                val mWalkPath: WalkPath = mWalkRouteResult.paths[0]
                tvFistLine.text =
                    "${MapUtil.getFriendlyTimes(mWalkPath.duration.toInt())} ( ${MapUtil.getFriendlyLength(mWalkPath.distance.toInt())} )"

                val mAdapter = RouteTypeAdapter<WalkStep>(3)
                mRecyclerView.adapter = mAdapter
                val steps=mWalkPath.steps
                mAdapter.setNewData(steps)
                steps.add(0, WalkStep())
                steps.add(WalkStep())
                size=steps.size
            }
            4 -> {
                val mRideRouteResult: RideRouteResult = intent.getParcelableExtra("RouteResult")
                val mRidePath: RidePath = mRideRouteResult.paths[0]
                tvFistLine.text =
                    "${MapUtil.getFriendlyTimes(mRidePath.duration.toInt())} ( ${MapUtil.getFriendlyLength(mRidePath.distance.toInt())} )"

                val mAdapter = RouteTypeAdapter<RideStep>(4)
                mRecyclerView.adapter = mAdapter
                val steps=mRidePath.steps
                mAdapter.setNewData(steps)
                steps.add(0, RideStep())
                steps.add(RideStep())
                size=steps.size
            }
        }

    }


    private inner class RouteTypeAdapter<T>(var type: Int) :
        BaseQuickAdapter<T, BaseViewHolder>(R.layout.item_type_route) {

        override fun convert(helper: BaseViewHolder, t: T) {

            when {
                helper.layoutPosition == 0 -> {
                    helper.setImageResource(R.id.bus_dir_icon, R.mipmap.dir_start)
                        .setText(R.id.bus_line_name, "出发")
                    helper.setGone(R.id.bus_dir_icon_up, false)
                    helper.setGone(R.id.bus_split_line, false)
                    helper.setVisible(R.id.bus_dir_icon_down, true)

                }
                helper.layoutPosition == size-1 -> {
                    helper.setImageResource(R.id.bus_dir_icon, R.mipmap.dir_end)
                        .setText(R.id.bus_line_name, "到达终点")
                    helper.setGone(R.id.bus_dir_icon_up, true)
                    helper.setGone(R.id.bus_split_line, true)
                    helper.setVisible(R.id.bus_dir_icon_down, false)

                }
                else -> {
                    if (type == 0) {
                        val item:DriveStep = t as DriveStep
                        val resID:Int = MapUtil.getDriveActionID(item.action)
                        helper.setImageResource(R.id.bus_dir_icon, resID).setText(R.id.bus_line_name, item.instruction)
                    }else if (type == 3) {
                        val item:WalkStep = t as WalkStep
                        val resID:Int = MapUtil.getDriveActionID(item.action)
                        helper.setImageResource(R.id.bus_dir_icon, resID).setText(R.id.bus_line_name, item.instruction)
                    }else if (type == 4) {
                        val item:RideStep = t as RideStep
                        val resID:Int = MapUtil.getDriveActionID(item.action)
                        helper.setImageResource(R.id.bus_dir_icon, resID).setText(R.id.bus_line_name, item.instruction)
                    }
                    helper.setGone(R.id.bus_dir_icon_up, true)
                    helper.setGone(R.id.bus_split_line, true)
                    helper.setVisible(R.id.bus_dir_icon_down, true)
                }
            }
        }

    }
}