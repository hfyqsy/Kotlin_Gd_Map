package com.hfjs.kotlin_mapuse.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import com.amap.api.maps.AMap
import com.hfjs.kotlin_mapuse.base.BaseActivity
import kotlinx.android.synthetic.main.include_map_view.*

open class BaseMapActivity(@LayoutRes override val layoutRes: Int) : BaseActivity(layoutRes) {
    protected lateinit var aMap: AMap
    override fun initView(bundle: Bundle?) {
        mMapView.onCreate(bundle)
        aMap = mMapView.map
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