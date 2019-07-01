package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseLocationActivity
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_choose_point.*
import kotlinx.android.synthetic.main.include_recycler_view.*

class ChoosePointActivity : BaseLocationActivity(R.layout.activity_choose_point) {

    private lateinit var mPoiItems: MutableList<PoiItem>//搜索结果集合
    private lateinit var mAdapter: PoiItemAdapter//适配器
    private lateinit var marker: Marker//中心marker
    private var mGeocodeSearch: GeocodeSearch? = null
    private lateinit var firstItem: PoiItem
    private lateinit var city: String
    private var mSearch: String = "住宅"

    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        initToolbar(intent.getIntExtra("title", 0))
        initMap(aMap)
        initLocation()
        initRecycleView()
    }

    /**
     * 设置点击监听事件
     */
    override fun setListener() {
        mHouse.setOnClickListener {
            mSearch = "住宅"
            geoAddress()
        }
        mSchool.setOnClickListener {
            mSearch = "学校"
            geoAddress()
        }
        mBuilding.setOnClickListener {
            mSearch = "楼宇"
            geoAddress()
        }
        mMarket.setOnClickListener {
            mSearch = "商场"
            geoAddress()
        }


    }

    /**
     * 初始化RecyclerView
     */
    override fun initRecycleView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = PoiItemAdapter()
        mRecyclerView.adapter = mAdapter
    }

    /**
     * 初始化map状态
     */
    private fun initMap(aMap: AMap) {

        val setting = aMap.uiSettings
        //设置是否手势缩放
        setting.isZoomControlsEnabled = false
        //设置默认定位按钮
        setting.isMyLocationButtonEnabled = true
        //设置默认定位按钮
        val locationStyle = MyLocationStyle()
        //定位蓝点展现模式
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        //设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。
        // 该方法只会作用在会执行连续定位的工作模式上。
        locationStyle.interval(2000)
        aMap.myLocationStyle = locationStyle
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.isMyLocationEnabled = true
        aMap.setOnMapLoadedListener { addMarkerInScreenCent() }
        //设置地图移动监听
        aMap.setOnCameraChangeListener(mapChangerListener)
        mGeocodeSearch = GeocodeSearch(this);
        mGeocodeSearch!!.setOnGeocodeSearchListener(geocodeSearchListener)

    }

    /**
     * 获取到定位经纬度
     */
    private var searchPoint: LatLonPoint? = null

    /**
     * 定位回调函数
     */
    override fun location(location: AMapLocation) {
        val latLng = LatLng(location.latitude, location.longitude)
        searchPoint = LatLonPoint(latLng.latitude, latLng.longitude)
        city = location.city
        //移动到定位地点 并放大到18级
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        Logger.e("定位城市== 》   " + location.city)
    }

    /**
     * 添加中间不移动marker
     */
    private fun addMarkerInScreenCent() {
        val latLng = aMap.cameraPosition.target
        val point = aMap.projection.toScreenLocation(latLng)
        marker = aMap.addMarker(
            MarkerOptions().anchor(
                0.5f,
                0.5f
            ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin))
        )
        //设置marker在屏幕上，不跟随地图移动
        marker.setPositionByPixels(point.x, point.y)
        marker.zIndex = 1f
    }

    /**
     * 搜索Poi
     */
    private lateinit var mQuery: PoiSearch.Query
    private lateinit var mPoiSearch: PoiSearch
    private fun poiSearch(type: String) {
        mQuery = PoiSearch.Query(type, "", city)
        mQuery.cityLimit = true
        mQuery.pageSize = 20
        mQuery.pageNum = 0
        if (searchPoint == null) return
        mPoiSearch = PoiSearch(this, mQuery)
        //搜索监听
        mPoiSearch.setOnPoiSearchListener(poiSearchListener)
        //设置搜索范围
        mPoiSearch.bound = PoiSearch.SearchBound(searchPoint, 1000, true)
        mPoiSearch.searchPOIAsyn()

    }

    /**
     * 搜索结果监听
     */
    private val poiSearchListener = object : PoiSearch.OnPoiSearchListener {
        override fun onPoiSearched(p0: PoiResult?, p1: Int) {
            if (p1 != AMapException.CODE_AMAP_SUCCESS) {
                toast("搜索无结果")
                return
            }
            if (p0 == null || p0.query == null) return
            if (p0.query == mQuery) {
                mPoiItems = p0.pois
                if (mPoiItems.size < 0) {
                    toast("搜索无结果")
                    return
                }
                mPoiItems.add(0, firstItem)
                mAdapter.setNewData(mPoiItems)
            }
        }

        override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

        }
    }
    /**
     * 地图移动监听事件
     */
    private val mapChangerListener = object : AMap.OnCameraChangeListener {
        override fun onCameraChange(p0: CameraPosition?) {

        }

        override fun onCameraChangeFinish(p0: CameraPosition?) {
            geoAddress()
            searchPoint = LatLonPoint(p0!!.target.latitude, p0.target.longitude)
            Logger.e(searchPoint.toString())
        }

    }

    /**
     * 响应逆地理编码
     */
    private fun geoAddress() {
        if (searchPoint != null) {
            //第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            val query = RegeocodeQuery(searchPoint, 200f, GeocodeSearch.AMAP)
            mGeocodeSearch!!.getFromLocationAsyn(query)
        }
    }

    /**
     * 逆地理编码监听
     */
    private val geocodeSearchListener = object : GeocodeSearch.OnGeocodeSearchListener {
        override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
            if (p1 != AMapException.CODE_AMAP_SUCCESS) {
                toast("errorCode==> $p1")
                return
            }
            if (p0 == null) return
            val regeocodeAddress = p0.regeocodeAddress
            if (regeocodeAddress != null && regeocodeAddress.formatAddress != null) {
                val address = regeocodeAddress.province + regeocodeAddress.city +
                        regeocodeAddress.district + regeocodeAddress.township
                firstItem = PoiItem("regeo", searchPoint, address, address)
                poiSearch(mSearch)
            }
        }

        override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {

        }

    }

    /**
     * 列表适配器
     */
    class PoiItemAdapter : BaseQuickAdapter<PoiItem, BaseViewHolder>(R.layout.item_poi_item) {
        override fun convert(helper: BaseViewHolder, item: PoiItem) {
            helper.apply {
                setText(R.id.poi_item_title, item.title)
                setText(R.id.poi_item_addr, item.provinceName + item.cityName + item.adName)
            }
        }

    }


}
