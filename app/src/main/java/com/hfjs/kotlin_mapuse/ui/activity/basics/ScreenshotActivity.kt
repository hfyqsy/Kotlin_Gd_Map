package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.ScreenShotHelper
import kotlinx.android.synthetic.main.activity_screenshot.*
import kotlinx.android.synthetic.main.include_map_view.*

class ScreenshotActivity :BaseActivity(R.layout.activity_screenshot) {
    private lateinit var aMap: AMap
    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        initToolbar(intent.getIntExtra("title", 0))
        mMapView.onCreate(bundle)
        aMap = mMapView.map
        initMap()

    }
    private fun initMap(){
        val uiSettings = aMap.uiSettings
        uiSettings.isZoomControlsEnabled = false
        aMap.addPolyline(MapUtil.getTestData())
        aMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.Builder().include(
                    LatLng(39.991533, 116.379546)
                ).include(LatLng(40.025367, 116.407101)).build(), 0
            )
        )
    }
    override fun setListener() {
        mScreenshot.setOnClickListener {
            aMap.getMapScreenShot(screenListener);
        }
        mScreenshotShow.setOnClickListener {
            startActivity(ShowImgActivity::class.java)
        }
    }


    var screenListener=object :AMap.OnMapScreenShotListener{
        override fun onMapScreenShot(p0: Bitmap?, p1: Int) {

        }

        override fun onMapScreenShot(p0: Bitmap) {
            ScreenShotHelper.saveScreenShot(p0, mMapView, mMapView);
            Toast.makeText(getApplicationContext(),"SD卡下查看截图后的文件", Toast.LENGTH_SHORT).show();
        }



    }


    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }
}