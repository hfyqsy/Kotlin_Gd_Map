package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.os.Bundle
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_change_map.*
import kotlinx.android.synthetic.main.include_map_view.*


class ChangeMapActivity : BaseActivity(R.layout.activity_change_map) {
    private lateinit var aMap: AMap
    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        mTextureMapView.onCreate(bundle)
        aMap = mMapView.map
        initMap()
    }
    private fun initMap(){
        val uiSettings = aMap.getUiSettings()
        uiSettings.setZoomControlsEnabled(false)
        aMap.addPolyline(MapUtil.getTestData())
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
            LatLngBounds.Builder().include(LatLng(39.991533, 116.379546))
                .include(LatLng(40.025367, 116.407101)).build(), 0))
    }
    override fun setListener() {
        mMapState.setOnClickListener {
            if (mMapState.text == getString(R.string.desc_map_domestic))
                mMapState.setText(R.string.desc_map_abroad)
            else
                mMapState.setText(R.string.desc_map_domestic)
        }


    }


    override fun onResume() {
        super.onResume()
        mTextureMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mTextureMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTextureMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mTextureMapView.onSaveInstanceState(outState)
    }
}

