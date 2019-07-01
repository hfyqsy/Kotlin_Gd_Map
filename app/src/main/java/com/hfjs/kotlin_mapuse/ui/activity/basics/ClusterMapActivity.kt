package com.hfjs.kotlin_mapuse.ui.activity.basics

import android.content.Context
import android.os.Bundle
import android.view.View
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.maps.model.LatLngBounds.builder
import com.clustering.ClusterItem
import com.clustering.ClusterManager
import com.clustering.RegionItem
import com.clustering.view.DefaultClusterRenderer
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.MapUtil
import kotlinx.android.synthetic.main.activity_cluster_map.*
import kotlinx.android.synthetic.main.include_map_view.*
import kotlinx.android.synthetic.main.mark_layout.view.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class ClusterMapActivity : BaseActivity(R.layout.activity_cluster_map) {
    private lateinit var aMap: AMap
    private var mClusterItems: MutableList<RegionItem>? = null
    private var mClusterManager: ClusterManager<RegionItem>? = null
    private var mClusterRenderer: ClusterRenderer? = null
    private lateinit var mLruCache: HashMap<String, BitmapDescriptor>

    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        initToolbar(intent.getIntExtra("title", 0))
        initCache()
        mMapView.onCreate(bundle)
        aMap = mMapView.map
        initCluster()
        aMap.setInfoWindowAdapter(infoAdapter)

    }

    private fun initCache() {
        mLruCache = HashMap<String, BitmapDescriptor>()
    }


    //添加数据
    override fun initData() {
        super.initData()
        mClusterItems = ArrayList()
        thread { addCluster(true) }.start()
    }

    //点击监听事件
    override fun setListener() {
        //聚合
        rdb_cluster.setOnClickListener {
            Logger.e("聚合")
            mClusterRenderer!!.minClusterSize = 2
//            addCluster(true)
            thread { addCluster(true) }.start()
        }
        //展开
        rdb_uncluster.setOnClickListener {
            Logger.e("展开")
            mClusterRenderer!!.minClusterSize = 100
//            addCluster(false)
            thread { addCluster(false) }.start()
        }
    }

    /**
     * 初始化点聚合
     */
    private fun initCluster() {
        mClusterManager = ClusterManager(this, aMap)
        mClusterRenderer = ClusterRenderer(this, aMap, mClusterManager!!)
        mClusterRenderer!!.minClusterSize = 2
        mClusterManager!!.renderer = mClusterRenderer
        //map状态改变监听
        aMap.setOnCameraChangeListener(mClusterManager)
        //marker 点击事件
        aMap.setOnMarkerClickListener(mClusterManager)

        mClusterManager!!.setOnClusterClickListener {
            val bundle = builder();
            for (item: ClusterItem in it.items) {
                bundle.include(item.position)
            }
            val bounds = bundle.build()
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            true
        }
        mClusterManager!!.setOnClusterItemClickListener {
            val marker = mClusterRenderer!!.getMarker(it)
            marker.showInfoWindow()
            MapUtil.animateMap(aMap, it.position)
            true
        }
    }

    /**
     * 添加聚合数据
     */
    private fun addCluster(isCluster: Boolean) {
        aMap.clear()
        mClusterItems!!.clear()
        mClusterManager!!.clearItems()
        for (i in 0..499) {
            mClusterItems!!.add(RegionItem(LatLng(Math.random() + 31.206080, Math.random() + 121.602948), "test $i"))
        }

        mClusterManager!!.addItems(mClusterItems)
        mClusterManager!!.cluster()
        if (isCluster) zoomToSpan(mClusterItems!!)
    }

    /**
     * 获取地图图标Bitmap
     */
    private fun getBitmap(title: String): BitmapDescriptor {
        val view = View.inflate(this, R.layout.mark_layout, null)
        view.tv_mark_name.text = title
        return BitmapDescriptorFactory.fromView(view)
    }

    //设置中心点
    private fun zoomToSpan(listBean: List<RegionItem>) {
        if (listBean.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            for (item in listBean) {
                builder.include(item.position)
            }
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200))
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
            val view = View.inflate(this@ClusterMapActivity, R.layout.popup_marker, null)
            return view
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
        mLruCache.clear()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    // inner 修饰内部类
    private inner class ClusterRenderer(context: Context, map: AMap, clusterManager: ClusterManager<RegionItem>) :
        DefaultClusterRenderer<RegionItem>(context, map, clusterManager) {


        //单个图标
        protected override fun onBeforeClusterItemRendered(item: RegionItem, markerOptions: MarkerOptions) {
//            super.onBeforeClusterItemRendered(item, markerOptions)
            //默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
            var bitmapDesc = mLruCache.get(item.title);
            if (bitmapDesc == null) {
                bitmapDesc = getBitmap(item.title)
                mLruCache.put(item.title, bitmapDesc)
            }
            markerOptions.icon(bitmapDesc)
        }

    }


}