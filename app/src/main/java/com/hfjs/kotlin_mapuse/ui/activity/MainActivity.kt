package com.hfjs.kotlin_mapuse.ui.activity

import android.Manifest
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.MenuItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.entity.MainEntity
import com.hfjs.kotlin_mapuse.permission.PermissionReq
import com.hfjs.kotlin_mapuse.permission.PermissionResult
import com.hfjs.kotlin_mapuse.ui.activity.basics.*
import com.hfjs.kotlin_mapuse.ui.activity.cover.CloverTaggingActivity
import com.hfjs.kotlin_mapuse.ui.activity.location.ContinueLocationActivity
import com.hfjs.kotlin_mapuse.ui.activity.navi.NaviRouteActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.CalculateRouteActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.DriveActivity
import com.hfjs.kotlin_mapuse.ui.activity.route.RouteActivity
import com.hfjs.kotlin_mapuse.ui.activity.search.SearchActivity
import com.hfjs.kotlin_mapuse.ui.activity.trail.MangerTrailActivity
import com.hfjs.kotlin_mapuse.ui.activity.trail.MoveMarkerActivity
import com.hfjs.kotlin_mapuse.ui.activity.trail.SmoothActivity
import com.hfjs.kotlin_mapuse.ui.activity.trip.TripHostActivity
import com.hfjs.kotlin_mapuse.ui.adapter.MainAdapter
import com.hfjs.kotlin_mapuse.utils.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_recycler_view.*
import java.util.*

class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var mAdapter: MainAdapter
    private val testBeans = ArrayList<MainEntity>()

    override fun initView() {
        getPermission()
        initRecycleView()
    }

    override fun initRecycleView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MainAdapter(testBeans)
        mRecyclerView.adapter = mAdapter
    }

    override fun initData() {
        testBeans.add(MainEntity(R.string.title_basics))
        testBeans.add(MainEntity(R.string.title_basics_point, ChoosePointActivity::class.java))
        testBeans.add(MainEntity(R.string.title_basics_cluster, ClusterMapActivity::class.java))
        testBeans.add(MainEntity(R.string.title_basics_cluster2, ClusterOverActivity::class.java))
        testBeans.add(MainEntity(R.string.title_basics_screenshot, ScreenshotActivity::class.java))
        testBeans.add(MainEntity(R.string.title_basics_change, ChangeMapActivity::class.java))
        testBeans.add(MainEntity(R.string.title_cover))
        testBeans.add(MainEntity(R.string.title_cover_types, CloverTaggingActivity::class.java))
        testBeans.add(MainEntity(R.string.title_trail))
        testBeans.add(MainEntity(R.string.title_trail_manger, MangerTrailActivity::class.java))
        testBeans.add(MainEntity(R.string.title_trail_move, MoveMarkerActivity::class.java))
        testBeans.add(MainEntity(R.string.title_trail_handle, SmoothActivity::class.java))
        testBeans.add(MainEntity(R.string.title_search))
        testBeans.add(MainEntity(R.string.title_search, SearchActivity::class.java))
//        testBeans.add(MainEntity(R.string.title_interactive))
        testBeans.add(MainEntity(R.string.title_route))
        testBeans.add(MainEntity(R.string.title_route_many, CalculateRouteActivity::class.java))
        testBeans.add(MainEntity(R.string.title_route_drive, DriveActivity::class.java))
        testBeans.add(MainEntity(R.string.title_route_plan, RouteActivity::class.java))
        testBeans.add(MainEntity(R.string.title_location_navigation))
        testBeans.add(MainEntity(R.string.title_location_one, ContinueLocationActivity::class.java))
        testBeans.add(MainEntity(R.string.title_navigation_ghost, NaviRouteActivity::class.java))
        testBeans.add(MainEntity(R.string.title_trip_host, TripHostActivity::class.java))
        mAdapter.setNewData(testBeans)
    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        mAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, _, position ->
            val item = testBeans[position]
            startIntentActivity(item.getClazz(), item.title)
        }
    }

    /**
     * toolbar 部件点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (!mMainDrawer.isDrawerOpen(Gravity.START))
                mMainDrawer.openDrawer(Gravity.START)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getPermission() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )
        val result = object : PermissionResult {
            override fun onGranted() {
                Logger.e("请求成功")
            }

            override fun onDenied() {
                Logger.e("请求失败")
                finish()
            }
        }
        PermissionReq.with(this).permissions(permissions).result(result).request()

    }
}
