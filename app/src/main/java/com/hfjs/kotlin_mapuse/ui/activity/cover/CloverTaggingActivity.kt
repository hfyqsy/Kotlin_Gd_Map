package com.hfjs.kotlin_mapuse.ui.activity.cover

import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.animation.ScaleAnimation
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_map_marker.*


class CloverTaggingActivity : BaseMapActivity(R.layout.activity_map_marker) {
    private lateinit var markerOverlay: MarkerOverlay
    private var center = LatLng(39.993167, 116.473274)// 中心点
    /**
     * 初始化
     */
    override fun initView() {
        initToolbar(intent.getIntExtra("title", 0))
        val markerPosition = LatLng(39.993308, 116.473258)
        MapUtil.animateMap(aMap, markerPosition)
        val options = MarkerOptions()
        options.position(markerPosition)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark))
        val marker = aMap.addMarker(options)
        markerAnimation(marker)

        markerOverlay = MarkerOverlay(aMap, getPointList(), center)
        markerOverlay.addToMap()
        markerOverlay.zoomToSpanWithCenter()
    }


    /**
     * 设置监听
     */
    override fun setListener() {
        //文字标注
        cbMarkerText.setOnClickListener { aMap.showMapText(cbMarkerText.isChecked) }
        //3D楼块
        cbMarker3D.setOnClickListener { aMap.showBuildings(cbMarker3D.isChecked) }
        //室内地图
        cbMarkerIndoor.setOnClickListener { aMap.showIndoorMap(cbMarkerIndoor.isChecked) }

        cbSpan.setOnClickListener {
            zoomToSpan()
        }

        cbSpanCenter.setOnClickListener {
            zoomToSpanWithCenter()
        }
        //marker 点击事件
        aMap.setOnMarkerClickListener {
            it.startAnimation()
            true
        }

        aMap.setOnMapLongClickListener {
            center = it
            markerOverlay.setCenterPoint(center)
        }

        aMap.setOnMapClickListener {
            markerOverlay.addPoint(it)
        }

//        aMap.setOnMapLoadedListener {
//
//        }
    }

    private fun zoomToSpan() {
        markerOverlay.zoomToSpan()
    }

    private fun zoomToSpanWithCenter() {
        markerOverlay.zoomToSpanWithCenter()
    }

    /**
     * marker设置动画
     */
    private fun markerAnimation(marker: Marker) {
        val markerAnimation = ScaleAnimation(0f, 1f, 0f, 1f) //初始化生长效果动画
        markerAnimation.setDuration(1000)  //设置动画时间 单位毫秒
        marker.setAnimation(markerAnimation)
    }

    private fun getPointList(): List<LatLng> {
        val pointList = ArrayList<LatLng>()
        pointList.add(LatLng(39.993755, 116.467987))
        pointList.add(LatLng(39.985589, 116.469306))
        pointList.add(LatLng(39.990946, 116.48439))
        pointList.add(LatLng(40.000466, 116.463384))
        pointList.add(LatLng(39.975426, 116.490079))
        pointList.add(LatLng(40.016392, 116.464343))
        pointList.add(LatLng(39.959215, 116.464882))
        pointList.add(LatLng(39.962136, 116.495418))
        pointList.add(LatLng(39.994012, 116.426363))
        pointList.add(LatLng(39.960666, 116.444798))
        pointList.add(LatLng(39.972976, 116.424517))
        pointList.add(LatLng(39.951329, 116.455913))
        return pointList
    }

    override fun onDestroy() {
        super.onDestroy()
        markerOverlay.removeFromMap()
    }
}