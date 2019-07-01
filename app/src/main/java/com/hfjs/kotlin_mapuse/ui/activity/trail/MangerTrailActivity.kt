package com.hfjs.kotlin_mapuse.ui.activity.trail

import android.graphics.Color
import com.amap.api.location.AMapLocation
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.trace.LBSTraceClient
import com.amap.api.trace.TraceListener
import com.amap.api.trace.TraceLocation
import com.amap.api.trace.TraceOverlay
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseLocationActivity
import com.hfjs.kotlin_mapuse.entity.PathRecordEntity
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.TraceUtil
import kotlinx.android.synthetic.main.activity_trail_manger.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MangerTrailActivity : BaseLocationActivity(R.layout.activity_trail_manger) {
    private var mTraceOverlay: TraceOverlay? = null
    private lateinit var mTraceList: MutableList<TraceOverlay>
    private lateinit var mTraceLocationList: MutableList<TraceLocation>
    private lateinit var mPolylineOptions: PolylineOptions
    private lateinit var tracePolyOption: PolylineOptions
    private val traceSize = 30//
    private var mDistance = 0//总距离
    private var mRecord: PathRecordEntity? = null
    private var startTime: Long = 0//开始时间
    private var endTime: Long = 0//结束时间
    private var polyLine: Polyline? = null
    private var mLocMarker: Marker? = null


    override fun initView() {
        isEnd = false
        initLocation()
        mTraceList = ArrayList()
        mTraceLocationList = ArrayList()
        initPolyline()
        mTraceOverlay = TraceOverlay(aMap)
    }

    override fun location(location: AMapLocation) {
        val latlng = LatLng(location.latitude, location.longitude)
        Logger.e("经纬度== $latlng")
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latlng));
        if (btnToggle.isChecked) {
            mRecord!!.addPoint(location)
            mPolylineOptions.add(latlng)
            mTraceLocationList.add(TraceUtil.parseTraceLocation(location))
            Logger.e("经纬度== ${mPolylineOptions.points.size}")
            if (mPolylineOptions.points.size > 1) {
                if (polyLine != null) {
                    polyLine!!.points = mPolylineOptions.points;
                } else {
                    polyLine = aMap.addPolyline(mPolylineOptions);
                }
                if (mTraceLocationList.size > traceSize - 1) {
                    trace();
                }
            }
        }
    }

    override fun setListener() {
        btnToggle.setOnClickListener {
            if (btnToggle.isChecked) {
                aMap.clear(true)
                if (mRecord != null) mRecord = null
                mRecord = PathRecordEntity()
                startTime = System.currentTimeMillis()
                mRecord!!.mDate = getCueDate(startTime)
                tvAllDis.setText(R.string.desc_all_dis)
            } else {
                endTime = System.currentTimeMillis()
                mTraceList.add(mTraceOverlay!!)
                val decimalFormat = DecimalFormat("0.0")
                tvAllDis.text =
                    getString(R.string.desc_all_dis) + decimalFormat.format(getTotalDistance() / 1000.0) + "KM"
                val lbsTraceClient = LBSTraceClient.getInstance(application)
                lbsTraceClient.queryProcessedTrace(
                    2,
                    TraceUtil.parseTraceLocationList(mRecord!!.mPathLinePoints),
                    LBSTraceClient.TYPE_AMAP,
                    traceListener
                )
                saveRecord(mRecord!!.mPathLinePoints, mRecord!!.mDate)
            }
        }
    }

    private fun saveRecord(list: List<AMapLocation>, time: String) {
        Logger.e("${list.size} $time")
    }

    /**
     * 初始化
     */
    private fun initPolyline() {
        mPolylineOptions = PolylineOptions()
        mPolylineOptions.width(10f)
        mPolylineOptions.color(Color.GRAY)
        tracePolyOption = PolylineOptions()
        tracePolyOption.width(40f)
        tracePolyOption.customTexture = BitmapDescriptorFactory.fromResource(R.mipmap.grasp_trace_line)
    }

    /**
     * 轨迹纠偏
     */
    private val traceListener = object : TraceListener {

        override fun onRequestFailed(p0: Int, p1: String?) {
            mTraceList.add(mTraceOverlay!!);
            mTraceOverlay = TraceOverlay(aMap);
        }

        override fun onTraceProcessing(p0: Int, p1: Int, p2: MutableList<LatLng>?) {

        }

        /**
         * 轨迹纠偏成功
         * p0  纠偏路线的ID
         * p1  纠偏结果
         * p2  总距离
         * p3  等待时间
         */
        override fun onFinished(p0: Int, p1: MutableList<LatLng>?, p2: Int, p3: Int) {
            if (p0 == 1) {
                if (p1!!.size > 0) {
                    mTraceOverlay!!.add(p1!!)
                    mDistance += p2
                    mTraceOverlay!!.distance += p2
                    if (mLocMarker == null) {
                        mLocMarker = aMap.addMarker(
                            MarkerOptions().position(p1[p1.size - 1])
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car))
                                .title("看看还有 $mDistance 米")
                        )
                        mLocMarker!!.showInfoWindow()
                    } else {
                        mLocMarker!!.title = "再看看还有 $mDistance 米"
                        mLocMarker!!.position = p1[p1.size - 1]
                        mLocMarker!!.showInfoWindow()
                    }
                }
            } else if (p0 == 2) {
                if (p1!!.size > 0) {
                    aMap.addPolyline(PolylineOptions().color(Color.RED).width(40f).addAll(p1));
                }
            }
        }

    }

    /**
     * 格式化时间
     */
    private fun getCueDate(time: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd  HH:mm:ss ", Locale.getDefault())
        val curDate = Date(time)
        return formatter.format(curDate)
    }

    /**
     * 最后获取总距离
     * @return
     */
    private fun getTotalDistance(): Int {
        var distance = 0
        for (to in mTraceList) {
            distance += to.distance
        }
        return distance
    }

    private fun trace() {
        val locationList = ArrayList(mTraceLocationList)
        val mTraceClient = LBSTraceClient(applicationContext)
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, traceListener)
        val lastlocation = mTraceLocationList.get(mTraceLocationList.size - 1)
        mTraceLocationList.clear()
        mTraceLocationList.add(lastlocation)
    }
}