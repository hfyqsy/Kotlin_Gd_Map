package com.hfjs.kotlin_mapuse.ui.activity.search

import android.graphics.BitmapFactory
import android.view.View
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.PoiItem
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseLocationActivity
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*


class SearchActivity : BaseLocationActivity(R.layout.activity_search) {
    private lateinit var mPoiResult: PoiResult
    private lateinit var mQuery: PoiSearch.Query
    private lateinit var mSearch: PoiSearch
    private lateinit var mMarker: Marker
    private  var mPoiOverlay: PoiOverlay?=null
    private var mCity: String = ""
    override fun initView() {
        initLocation()
        aMap.uiSettings.isRotateGesturesEnabled = false
        aMap.setInfoWindowAdapter(infoAdapter)
    }

    override fun initData() {

    }

    /**
     * 开始搜索数据
     */
    private fun doSearchQuery(keyWorld: String) {
        mQuery = PoiSearch.Query(keyWorld, "", mCity)
        mQuery.pageSize = 10
        mQuery.pageNum = 0

        mSearch = PoiSearch(this, mQuery)
        mSearch.searchPOIAsyn()
        mSearch.setOnPoiSearchListener(poiSearchListener)
    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        tvSearch.setOnClickListener {
            val keyWorld = editSearch.text.toString()
            doSearchQuery(keyWorld)
        }
        //地图marker点击事件
        aMap.setOnMarkerClickListener {
            it.showInfoWindow()
            false
        }

    }

    override fun location(location: AMapLocation) {
        mCity = location.city
        val latLng = LatLng(location.latitude, location.longitude)
        mMarker = aMap.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location))
        )
        MapUtil.moveMap(aMap, latLng)
    }

    /**
     * 弹窗适配器
     */
    private var infoAdapter = object : AMap.InfoWindowAdapter {
        override fun getInfoContents(p0: Marker?): View? {
            return null
        }

        override fun getInfoWindow(p0: Marker?): View {
            return getInfoView(p0!!)
        }

        private fun getInfoView(marker: Marker?): View {
            val view = View.inflate(this@SearchActivity, R.layout.popup_marker, null)
            return view
        }

    }

    private var poiSearchListener = object : PoiSearch.OnPoiSearchListener {
        override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

        }

        override fun onPoiSearched(p0: PoiResult?, p1: Int) {
            if (p1 != AMapException.CODE_AMAP_SUCCESS) {
                toast("搜索无结果")
                return
            }
            if (p0 == null || p0.query == null) return
            if (p0.query == mQuery) {
                mPoiResult = p0
                val poiItems = p0.pois
                if (poiItems.size > 0) {
                    aMap.clear()
                    if (mPoiOverlay!=null)mPoiOverlay!!.destroy()
                    mPoiOverlay = PoiOverlay(aMap, poiItems)
                    mPoiOverlay!!.addToMap()
                    mPoiOverlay!!.zoomToSpan()

                }
            }
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private fun showSuggestCity(cities: List<SuggestionCity>) {
        var information = "推荐城市\n"
        for (i in cities.indices) {
            information += ("城市名称:" + cities[i].cityName + "城市区号:"
                    + cities[i].cityCode + "城市编码:"
                    + cities[i].adCode + "\n")
        }
        toast(information)

    }

    override fun onDestroy() {
        super.onDestroy()
        mPoiOverlay!!.destroy()
    }

    private inner class PoiOverlay internal constructor(
        private val aMap: AMap?, private var poiItems: MutableList<PoiItem>?
    ) {
        private var mPoiMarks: MutableList<Marker>? = null

        /**
         * 所有点显示地图缩放级别
         *
         * @return
         */
        private val latLngBounds: LatLngBounds
            get() {
                val builder = LatLngBounds.builder()
                if (poiItems != null) {
                    for (item in poiItems!!) {
                        builder.include(LatLng(item.latLonPoint.latitude, item.latLonPoint.longitude))
                    }
                }
                return builder.build()
            }

        private val markers = intArrayOf(
            R.mipmap.poi_marker_1,
            R.mipmap.poi_marker_2,
            R.mipmap.poi_marker_3,
            R.mipmap.poi_marker_4,
            R.mipmap.poi_marker_5,
            R.mipmap.poi_marker_6,
            R.mipmap.poi_marker_7,
            R.mipmap.poi_marker_8,
            R.mipmap.poi_marker_9,
            R.mipmap.poi_marker_10
        )

        init {
            mPoiMarks = ArrayList()
        }

        /**
         * 添加marker到地图上
         */
        fun addToMap() {
            Logger.e("poiItems=uuuu   $poiItems")
            if (poiItems == null || aMap == null) return
            Logger.e("poiItems=   "+poiItems!!.size)
            for ((i, item) in poiItems!!.withIndex()) {
                val marker = aMap.addMarker(getMarkerOptions(item, i))
                marker.setObject(item)
                mPoiMarks!!.add(marker)
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker
         */
        fun removeMarkersFromMap() {
            for (mark in mPoiMarks!!) {
                mark.remove()
            }
            mPoiMarks!!.clear()
        }

        /**
         * 销毁
         */
        fun destroy() {
            if (poiItems != null) {
                poiItems!!.clear()
                poiItems = null
            }
            if (mPoiMarks != null) {
                removeMarkersFromMap()
                mPoiMarks = null
            }
        }

        /**
         * 移动镜头到当前的视角。
         */
        fun zoomToSpan() {
            if (aMap == null) return
            if (poiItems != null && poiItems!!.size > 0) {
                val bounds = latLngBounds
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))
            }
        }

        /**
         * 设置marker点信息
         *
         * @param item
         * @param index
         * @return
         */
        private fun getMarkerOptions(item: PoiItem, index: Int): MarkerOptions {
            val point = item.latLonPoint
            return MarkerOptions().position(LatLng(point.latitude, point.longitude))
                .icon(getBitmapDescriptor(index))
        }

        /**
         * 获取marker背景
         *
         * @param index
         * @return
         */
        private fun getBitmapDescriptor(index: Int): BitmapDescriptor? {
            return BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(resources, markers[index]))
        }
    }
}