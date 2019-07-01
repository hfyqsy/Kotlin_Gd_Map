package com.hfjs.kotlin_mapuse.ui.activity.route

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.amap.api.services.busline.BusStationItem
import com.amap.api.services.route.BusPath
import com.amap.api.services.route.BusRouteResult
import com.amap.api.services.route.BusStep
import com.amap.api.services.route.RailwayStationItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.entity.BusStepEntity
import com.hfjs.kotlin_mapuse.ui.activity.route.ovrelay.BusOverlay
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_drive_route.*
import kotlinx.android.synthetic.main.include_map_view.*
import kotlinx.android.synthetic.main.include_recycler_view.*
import kotlinx.android.synthetic.main.include_toolbar.*

class BusDetailActivity : BaseMapActivity(R.layout.activity_route_detail) {

    private var size: Int = 0
    override fun initView() {
        initToolbar("路线详情", "地图")
    }

    override fun initData() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        val mBusRouteResult: BusRouteResult = intent.getParcelableExtra("RouteResult")
        val index = intent.getIntExtra("position", 0);
        val mBusPath: BusPath = mBusRouteResult.paths[index]

        tvFistLine.text =
            "${MapUtil.getFriendlyTimes(mBusPath.duration.toInt())} ( ${MapUtil.getFriendlyLength(mBusPath.distance.toInt())} )"
        tvSecondLine.text = "打车约 ${mBusRouteResult.taxiCost}元"

        val mAdapter = RouteTypeAdapter(initDatas(mBusPath.steps))
        mRecyclerView.adapter = mAdapter


        initMapView(mBusRouteResult,index)

    }

    private fun initDatas(list: List<BusStep>): MutableList<BusStepEntity> {
        val stepList = ArrayList<BusStepEntity>()
        val start = BusStepEntity(null)
        start.isStart = true
        stepList.add(start)
        for (step: BusStep in list) {
            if (step.walk != null && step.walk.distance > 0) {
                val walk = BusStepEntity(step)
                walk.isWalk = true
                stepList.add(walk)
            }
            if (step.busLine != null) {
                val bus = BusStepEntity(step)
                bus.isBus = true
                stepList.add(bus)
            }
            if (step.railway != null) {
                val railway = BusStepEntity(step)
                railway.isRailway = true
                stepList.add(railway)
            }

            if (step.taxi != null) {
                val taxi = BusStepEntity(step)
                taxi.isTaxi = true
                stepList.add(taxi)
            }
        }
        val end = BusStepEntity(null)
        start.isEnd = true
        stepList.add(end)
        size = stepList.size

        return stepList
    }

    private var mBusOverlay: BusOverlay? = null
    private fun initMapView(result: BusRouteResult,index:Int) {
        if (mBusOverlay != null) mBusOverlay!!.removeFromMap()
        mBusOverlay = BusOverlay(aMap, result.startPos, result.targetPos, result.paths[index])
        mBusOverlay!!.addToMap()
        mBusOverlay!!.zoomToSpan()
    }


    override fun setListener() {
        mToolbarTvAction.setOnClickListener {
            if (mToolbarTvAction.text == "地图") {
                mToolbarTvAction.text = "列表"
                mRecyclerView.visibility = View.VISIBLE
                mMapView.visibility = View.GONE
            } else {
                mToolbarTvAction.text = "地图"
                mMapView.visibility = View.VISIBLE
                mRecyclerView.visibility = View.GONE
            }
        }
    }

    /**
     * 设置标题
     */
    private fun initToolbar(title: String, action: String) {
        mToolbar!!.title = ""
        mToolbarTitle!!.text = title
        mToolbarTvAction.text = action
        mToolbarTvAction.visibility = View.VISIBLE
        setSupportActionBar(mToolbar!!)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_white)
    }

    private inner class RouteTypeAdapter(data: MutableList<BusStepEntity>) :
        BaseQuickAdapter<BusStepEntity, BaseViewHolder>(R.layout.item_type_route, data) {

        override fun convert(helper: BaseViewHolder, item: BusStepEntity) {
            val holder = ViewHolder(helper.itemView)
            when {
                helper.layoutPosition == 0 -> {
                    holder.busDirIcon.setImageResource(R.mipmap.dir_start)
                    holder.busLineName.text = "出发"
                    holder.busDirUp.visibility = View.INVISIBLE
                    holder.busDirDown.visibility = View.VISIBLE
                    holder.splitLine.visibility = View.GONE
                    holder.busStationNum.visibility = View.GONE
                    holder.busExpandImage.visibility = View.GONE
                }
                helper.layoutPosition == size - 1 -> {
                    holder.busDirIcon.setImageResource(R.mipmap.dir_end)
                    holder.busLineName.text = "到达终点"
                    holder.busDirUp.visibility = View.VISIBLE
                    holder.busDirDown.visibility = View.INVISIBLE
                    holder.busStationNum.visibility = View.INVISIBLE
                    holder.busExpandImage.visibility = View.INVISIBLE
                }
                else -> {
                    if (item.isWalk && item.walk != null && item.walk.distance > 0) {
                        holder.busDirIcon.setImageResource(R.mipmap.dir13)
                        holder.busDirUp.visibility = View.VISIBLE
                        holder.busDirDown.visibility = View.VISIBLE
                        holder.busLineName.text = ("步行"
                                + item.walk.distance .toInt() + "米")
                        holder.busStationNum.visibility = View.GONE
                        holder.busExpandImage.visibility = View.GONE
                        return
                    } else if (item.isBus && item.busLines.size > 0) {
                        holder.busDirIcon.setImageResource(R.mipmap.dir14)
                        holder.busDirUp.visibility = View.VISIBLE
                        holder.busDirDown.visibility = View.VISIBLE
                        holder.busLineName.text = item.busLines[0].busLineName
                        holder.busStationNum.visibility = View.VISIBLE
                        holder.busStationNum.text = (item.busLines[0].passStationNum + 1).toString() + "站"
                        holder.busExpandImage.visibility = View.VISIBLE
                        val arrowClick = ArrowClick(holder, item)
                        holder.parent.tag = holder.layoutPosition
                        holder.parent.setOnClickListener(arrowClick)
                        return
                    } else if (item.isRailway && item.railway != null) {
                        holder.busDirIcon.setImageResource(R.mipmap.dir16)
                        holder.busDirUp.visibility = View.VISIBLE
                        holder.busDirDown.visibility = View.VISIBLE
                        holder.busLineName.text = item.railway.name
                        holder.busStationNum.visibility = View.VISIBLE
                        holder.busStationNum.setText("${item.railway.viastops.size + 1}  站")
                        holder.busExpandImage.visibility = View.VISIBLE
                        val arrowClick = ArrowClick(holder, item)
                        holder.parent.tag = holder.layoutPosition
                        holder.parent.setOnClickListener(arrowClick)
                        return
                    } else if (item.isTaxi && item.taxi != null) {
                        holder.busDirIcon.setImageResource(R.mipmap.dir14)
                        holder.busDirUp.visibility = View.VISIBLE
                        holder.busDirDown.visibility = View.VISIBLE
                        holder.busLineName.text = "打车到终点"
                        holder.busStationNum.visibility = View.GONE
                        holder.busExpandImage.visibility = View.GONE
                        return
                    }
                }
            }
        }

        private inner class ViewHolder(view: View) : BaseViewHolder(view) {
            internal var arrowExpend = false
            internal val expandContent: LinearLayout = view.findViewById(R.id.expand_content)
            internal var parent: RelativeLayout = view.findViewById(R.id.bus_item);
            internal var busLineName: TextView = view.findViewById(R.id.bus_line_name);
            internal var busDirIcon: ImageView = view.findViewById(R.id.bus_dir_icon);
            internal var busStationNum: TextView = view.findViewById(R.id.bus_station_num);
            internal var busExpandImage: ImageView = view.findViewById(R.id.bus_expand_image);
            internal var busDirUp: ImageView = view.findViewById(R.id.bus_dir_icon_up);
            internal var busDirDown: ImageView = view.findViewById(R.id.bus_dir_icon_down);
            internal var splitLine: ImageView = view.findViewById(R.id.bus_split_line);
        }

        private inner class ArrowClick(private val mHolder: ViewHolder, private var mItem: BusStepEntity) :
            View.OnClickListener {

            override fun onClick(v: View) {
                if (mItem.isBus) {
                    if (!mHolder.arrowExpend) {
                        mHolder.arrowExpend = true
                        mHolder.busExpandImage.setImageResource(R.mipmap.up)
                        addBusStation(mItem.busLines[0].departureBusStation)
                        for (station in mItem.busLines[0].passStations) {
                            addBusStation(station)
                        }
                        addBusStation(mItem.busLine.arrivalBusStation)

                    } else {
                        mHolder.arrowExpend = false
                        mHolder.busExpandImage.setImageResource(R.mipmap.down)
                        mHolder.expandContent.removeAllViews()
                    }
                } else if (mItem.isRailway) {
                    if (!mHolder.arrowExpend) {
                        mHolder.arrowExpend = true
                        mHolder.busExpandImage.setImageResource(R.mipmap.up)
                        addRailwayStation(mItem.railway.departurestop)
                        for (station in mItem.railway.viastops) {
                            addRailwayStation(station)
                        }
                        addRailwayStation(mItem.railway.arrivalstop)

                    } else {
                        mHolder.arrowExpend = false
                        mHolder.busExpandImage.setImageResource(R.mipmap.down)
                        mHolder.expandContent.removeAllViews()
                    }
                }


            }

            private fun addBusStation(station: BusStationItem) {
                val ll = View.inflate(this@BusDetailActivity, R.layout.item_bus_segment, null) as LinearLayout
                val tv = ll.findViewById<View>(R.id.bus_line_station_name) as TextView
                tv.text = station.busStationName
                mHolder.expandContent.addView(ll)
            }

            private fun addRailwayStation(station: RailwayStationItem) {
                val ll = View.inflate(this@BusDetailActivity, R.layout.item_bus_segment, null) as LinearLayout
                val tv = ll.findViewById<View>(R.id.bus_line_station_name) as TextView
                tv.text =   "${station.name}  ${getRailwayTime(station.time)}"
                mHolder.expandContent.addView(ll)
            }
        }

        fun getRailwayTime(time: String): String {
            return time.substring(0, 2) + ":" + time.substring(2, time.length)
        }
    }


}