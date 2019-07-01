package com.hfjs.kotlin_mapuse.ui.activity.trail

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.utils.overlay.SmoothMoveMarker
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil.coords
import kotlinx.android.synthetic.main.activity_move_marker.*


class MoveMarkerActivity : BaseMapActivity(R.layout.activity_move_marker) {
    private lateinit var mMoveMarker: SmoothMoveMarker
    private var list: List<LatLng>? = null
    override fun initView() {
        mMoveMarker = SmoothMoveMarker(aMap)
        
    }

    override fun initData() {
        addPolylineInPlayGround()
        val points = readLatLngs()
        val b = LatLngBounds.builder()
        for (i in points.indices) {
            b.include(points[i])
        }
        val bounds = b.build()
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


        // 设置滑动的图标
        mMoveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car))

        /*
        //当移动Marker的当前位置不在轨迹起点，先从当前位置移动到轨迹上，再开始平滑移动
        // LatLng drivePoint = points.get(0);//设置小车当前位置，可以是任意点，这里直接设置为轨迹起点
        LatLng drivePoint = new LatLng(39.980521,116.351905);//设置小车当前位置，可以是任意点
        Pair<Integer, LatLng> pair = PointsUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());
        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);*/

        mMoveMarker.setPoints(points)//设置平滑移动的轨迹list
        mMoveMarker.setTotalDuration(40)//设置平滑移动的总时间

        aMap.setInfoWindowAdapter(infoWindowAdapter)
        mMoveMarker.marker.showInfoWindow()
    }

    override fun setListener() {
        tvMoveStart.setOnClickListener {
            mMoveMarker.startSmoothMove();
        }
        tvMovePause.setOnClickListener {
            mMoveMarker.stopMove();
        }
        tvMoveStop.setOnClickListener {
            mMoveMarker.position = LatLng(39.97617053371078, 116.3499049793749)
            mMoveMarker.setPoints(list)
            mMoveMarker.stopMove();
            mMoveMarker.resetIndex()
        }
        mMoveMarker.setMoveListener {
            runOnUiThread {
                if (infoWindowLayout != null && title != null && mMoveMarker.marker.isInfoWindowShown) {
                    title!!.text = "距离终点还有： $it 米";
                }
                if (it == 0.0) {
                    mMoveMarker.marker.hideInfoWindow();

                }
            }
        }
    }


    private fun addPolylineInPlayGround() {
        list = readLatLngs()
        val colorList = ArrayList<Int>()

        aMap.addPolyline(
            PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.grasp_trace_line)) //setCustomTextureList(bitmapDescriptors)
                .addAll(list)
                .useGradient(true)
                .width(18f)
        )
    }

    private fun readLatLngs(): List<LatLng> {
        val points = ArrayList<LatLng>()
        var i = 0
        while (i < coords.size) {
            points.add(LatLng(coords[i + 1], coords[i]))
            i += 2
        }
        return points
    }

    var infoWindowLayout: LinearLayout? = null
    var title: TextView? = null
    var snippet: TextView? = null

    private fun getInfoWindowView(marker: Marker): View {
        if (infoWindowLayout == null) {
            infoWindowLayout = LinearLayout(this@MoveMarkerActivity)
            infoWindowLayout!!.orientation = LinearLayout.VERTICAL
            title = TextView(this@MoveMarkerActivity)
            snippet = TextView(this@MoveMarkerActivity)
            title!!.setTextColor(Color.BLACK)
            snippet!!.setTextColor(Color.BLACK)
            infoWindowLayout!!.setBackgroundResource(R.drawable.amu_bubble_shadow)

            infoWindowLayout!!.addView(title)
            infoWindowLayout!!.addView(snippet)
        }

        return infoWindowLayout!!
    }

    var infoWindowAdapter = object : AMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View {

            return getInfoWindowView(marker)
        }

        override fun getInfoContents(marker: Marker): View {


            return getInfoWindowView(marker)
        }
    }


}