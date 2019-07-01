package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.content.Context
import android.os.Bundle
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.Marker
import com.clusters.ClusterItem
import com.clusters.ClusterOverlay
import com.clusters.RegionItem
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.Logger
import kotlinx.android.synthetic.main.activity_cluster_map.*
import kotlinx.android.synthetic.main.include_map_view.*

class ClusterOverActivity : BaseActivity(R.layout.activity_cluster_map) {
    private lateinit var aMap: AMap
    private var mClusterItems: MutableList<RegionItem>? = null
    private lateinit var mClusterOver: ClusterOverlay

    override fun initView(bundle: Bundle?) {
        mMapView.onCreate(bundle)
        initToolbar(intent.getIntExtra("title", 0))
        aMap = mMapView.map
        aMap.setInfoWindowAdapter(infoAdapter)
        aMap.setOnInfoWindowClickListener {
            if (it.isInfoWindowShown) {
                it.hideInfoWindow()//这个是隐藏infowindow窗口的方法
            }
        }
        initClusterOver()
    }

    /**
     * 初始化聚合
     */
    private fun initClusterOver() {
        mClusterOver = ClusterOverlay(aMap, dp2px(this, 100f), this)
        mClusterOver.setOnClusterClickListener { marker, clusterItems ->
            val builder = LatLngBounds.Builder()
            for (clusterItem in clusterItems) {
                builder.include(clusterItem.position)
            }
            val latLngBounds = builder.build()
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100))
        }

        mClusterOver.setOnClusterItemClickListener { marker, clusterItem ->
            marker.showInfoWindow()
            aMap.animateCamera(CameraUpdateFactory.newLatLng(clusterItem.getPosition()))
        }
    }

    /**
     * 添加数据
     */
    override fun initData() {
        if (mClusterItems == null) {
            mClusterItems = ArrayList()
        }
        mClusterItems!!.clear()
        for (i in 0..499) {
            mClusterItems!!.add(RegionItem(LatLng(Math.random() + 31.206080, Math.random() + 121.602948), "test $i"))
        }
        Logger.e("mClusterItems=  ${mClusterItems!!.size}")
        mClusterOver.setMorePoint(mClusterItems as List<ClusterItem>?)
        initMapBounds()
    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        super.setListener()
        //聚合
        rdb_cluster.setOnClickListener {
            Logger.e("聚合")
         mClusterOver.updateClusters(false)
        }
        //展开
        rdb_uncluster.setOnClickListener {
            Logger.e("展开")
            mClusterOver.updateClusters(true)
        }
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
            val view = View.inflate(this@ClusterOverActivity, R.layout.popup_marker, null)
            return view
        }

    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 将聚合点显示到一屏上
     */
    private fun initMapBounds() {
        val builder = LatLngBounds.Builder()
        for (clusterItem in mClusterItems!!) {
            builder.include(clusterItem.getPosition())
        }
        val latLngBounds = builder.build()
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))
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
//        mLruCache.clear()
        mClusterOver.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }


}