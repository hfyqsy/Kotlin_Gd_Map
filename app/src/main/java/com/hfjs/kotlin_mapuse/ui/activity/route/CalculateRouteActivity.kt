package com.hfjs.kotlin_mapuse.ui.activity.route

import android.content.Intent
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.navi.model.AMapNaviPath
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.navi.view.RouteOverLay
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseNaviActivity
import com.hfjs.kotlin_mapuse.entity.StrategyEntity
import com.hfjs.kotlin_mapuse.utils.Logger
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_route_calculate.*
import java.io.Serializable

class CalculateRouteActivity : BaseNaviActivity(R.layout.activity_route_calculate), AMap.OnMapLoadedListener {

    private lateinit var mStrategy: StrategyEntity
    /**
     * 起点坐标
     */
    private val startLatLng = NaviLatLng(39.993537, 116.472875)
    /**
     * 终点坐标
     */
    private val endLatLng = NaviLatLng(39.90759, 116.392582)
    /**
     * 起点集合
     */
    private var startList = ArrayList<NaviLatLng>()
    /**
     * 途经点集合
     */
    private var wayList = ArrayList<NaviLatLng>()
    /**
     * 终点集合
     */
    private var endList = ArrayList<NaviLatLng>()
    /**
     * 保存当前算好的路线
     */
    private val routeOverlays = SparseArray<RouteOverLay>()
    /**
     * 路径ID
     */
    private var routeId: Int = -1
    private val ROUTE_UNSELECTED_TRANSPARENCY = 0.3f
    private val ROUTE_SELECTED_TRANSPARENCY = 1f
    private val REQUEST_CODE = 1001
    /**
     * 初始化界面
     */
    override fun initView() {
        initToolbar(intent.getIntExtra("title", 0))
        aMap.isTrafficEnabled = false
        aMap.setOnMapLoadedListener(this)
        aMap.uiSettings.isZoomControlsEnabled = false
        map_traffic.setImageResource(R.mipmap.map_traffic_white)
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        mStrategy = StrategyEntity(false, false, false, false, false)
        startList.add(startLatLng)
        endList.add(endLatLng)

    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        //路况设置
        map_traffic.setOnClickListener { setTraffic() }
        //偏好设置
        strategy_choose.setOnClickListener {
            startActivityForResult(Intent(this,StrategyActivity::class.java)
                .putExtra("title",R.string.title_route_strategy)
                .putExtra("strategy",mStrategy),REQUEST_CODE)
        }
        //导航
        btnNavi.setOnClickListener { startIntentActivity(RouteNaviActivity::class.java ,R.string.title_route_strategy)}
        llTypeOne.setOnClickListener { focusRoute(one = true, two = false, three = false) }
        llTypeTwo.setOnClickListener { focusRoute(one = false, two = true, three = false) }
        llTypeThree.setOnClickListener { focusRoute(one = false, two = false, three = true) }

    }

    /**
     *
     */
    private fun drawRoutes(routeId: Int, path: AMapNaviPath) {
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0f))
        val routeOverly = RouteOverLay(aMap, path, this)
        routeOverly.width = 60f
        routeOverly.isTrafficLine = true
        routeOverly.addToMap()
        routeOverlays.put(routeId, routeOverly)
    }

    override fun onMapLoaded() {
        Logger.e("地图加载==   ${mStrategy.cost}")
        val strategyFlag = mAMapNavi.strategyConvert(
            mStrategy.congestion,
            mStrategy.cost,
            mStrategy.avoidHeightSpeed,
            mStrategy.heightSpeed,
            true
        )
        mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag)
    }

    private fun setTraffic() {
        if (aMap.isTrafficEnabled) {
            map_traffic.setImageResource(R.mipmap.map_traffic_white)
            aMap.isTrafficEnabled = false
        } else {
            map_traffic.setImageResource(R.mipmap.map_traffic_hl_white)
            aMap.isTrafficEnabled = true
        }
    }

    /**
     * 清除路径
     */
    private fun cleanRouteOverlay() {
        for (i in 0 until routeOverlays.size()) {
            val key = routeOverlays.keyAt(i)
            val overlay = routeOverlays.get(key)
            overlay.removeFromMap()
            overlay.destroy()
        }
        routeOverlays.clear()
    }

    /**
     * 算路失败
     */
    override fun onCalculateRouteFailure(p0: Int) {
        toast("算路失败： 错误码  $p0")
    }

    /**
     * 算路成功
     */
    override fun onCalculateRouteSuccess(p0: IntArray?) {
        cleanRouteOverlay()
        val paths = mAMapNavi.naviPaths
        for (i in 0 until p0!!.size) {
            val path = paths[p0[i]]
            if (path != null) drawRoutes(p0[i], path)
        }

        setRouteLineTag(paths, p0)
        aMap.mapType = AMap.MAP_TYPE_NAVI
    }

    /**
     * 设置路径状态
     */
    private fun setLayout(
        routeId: Int, strategy: String, llType: LinearLayout,
        tvType: TextView, tvTime: TextView, tvMile: TextView
    ) {
        Logger.e("setLayout  $routeId  $strategy ")
        llType.tag = routeId
        val overly: RouteOverLay = routeOverlays.get(routeId)
        overly.zoomToSpan()
        val path: AMapNaviPath = overly.aMapNaviPath
        tvType.text = strategy
        tvTime.text = MapUtil.getFriendlyTime(path.allTime)
        tvMile.text = MapUtil.getFriendlyDistance(path.allLength)
    }

    /**
     * 设置路径状态
     */
    private fun setLayoutContent(type: Int, routeId: Int, strategy: String) {
        Logger.e("路径数据==  $routeId  $strategy")
        when (type) {
            0 -> setLayout(routeId, strategy, llTypeOne, tvTypeOne, tvTimeOne, tvMileOne)
            1 -> setLayout(routeId, strategy, llTypeTwo, tvTypeTwo, tvTimeTwo, tvMileTwo)
            2 -> setLayout(routeId, strategy, llTypeThree, tvTypeThree, tvTimeThree, tvMileThree)
        }
    }

    /**
     * 设置焦点View
     */
    private fun setFocusView(
        focus: Boolean, llType: LinearLayout,
        tvType: TextView, tvTime: TextView, tvMile: TextView
    ) {
        if (llType.visibility != View.VISIBLE) return
        Logger.e("测试==   " + llType.tag)
        val overlay: RouteOverLay = routeOverlays.get(llType.tag as Int)
        if (focus) {
            routeId = llType.tag as Int
            tvDescRoute.text = MapUtil.getRouteOverView(overlay.aMapNaviPath)
            mAMapNavi.selectRouteId(routeId)
            overlay.setTransparency(ROUTE_SELECTED_TRANSPARENCY)
            val color: Int = resources.getColor(R.color.color_349);
            tvType.setTextColor(color)
            tvTime.setTextColor(color)
            tvMile.setTextColor(color)
        } else {
            overlay.setTransparency(ROUTE_UNSELECTED_TRANSPARENCY)
            val color: Int = resources.getColor(R.color.color_333);
            tvType.setTextColor(color)
            tvTime.setTextColor(color)
            tvMile.setTextColor(color)
        }
    }

    /**
     * 设置获取焦点的View
     */
    private fun focusRoute(one: Boolean, two: Boolean, three: Boolean) {
        setFocusView(one, llTypeOne, tvTypeOne, tvTimeOne, tvMileOne)
        setFocusView(two, llTypeTwo, tvTypeTwo, tvTimeTwo, tvMileTwo)
        setFocusView(three, llTypeThree, tvTypeThree, tvTimeThree, tvMileThree)
    }

    /**
     * 设置路径Tag
     */
    private fun setRouteLineTag(paths: HashMap<Int, AMapNaviPath>, ints: IntArray) {

        Logger.e("数据    ${paths.size}")
        if (ints.isEmpty()) {
            focusRoute(one = false, two = false, three = false)
            return
        }

        val indexOne: Int = ints[0]
        val strategyOne: String = paths[indexOne]!!.labels
        setLayoutContent(0, indexOne, strategyOne)

        if (ints.size == 1) {
            setLayoutVisibility(one = true, two = false, three = false)
            focusRoute(one = true, two = false, three = false)
            return
        }

        val indexTwo: Int = ints[1]
        val strategyTwo: String = paths[indexTwo]!!.labels
        setLayoutContent(1, indexTwo, strategyTwo)
        if (ints.size == 2) {
            setLayoutVisibility(one = true, two = true, three = false)
            focusRoute(one = true, two = false, three = false)
            return
        }

        val indexThree: Int = ints[2]
        val strategyThree: String = paths[indexThree]!!.labels
        setLayoutContent(2, indexThree, strategyThree)
        if (ints.size == 3) {
            setLayoutVisibility(one = true, two = true, three = true)
            focusRoute(one = true, two = false, three = false)
        }
    }

    /**
     * 设置多路径的显示
     */
    private fun setLayoutVisibility(one: Boolean, two: Boolean, three: Boolean) {
        if (one) {
            llTypeOne.visibility = View.VISIBLE
        } else {
            llTypeOne.visibility = View.GONE
        }
        if (two) {
            llTypeTwo.visibility = View.VISIBLE
        } else {
            llTypeTwo.visibility = View.GONE
        }
        if (three) {
            llTypeThree.visibility = View.VISIBLE
        } else {
            llTypeThree.visibility = View.GONE
        }
    }

    private fun set() {

    }

    /**
     * 偏好设置返回
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.e("fanhui==    $requestCode   $resultCode   $data")
        if (REQUEST_CODE == requestCode && data != null) {
            mStrategy=data.getSerializableExtra("strategy") as  StrategyEntity
            Logger.e(mStrategy.congestion)
//            mStrategy.congestion = data.getBooleanExtra("congestion", false)
//            mStrategy.cost = data.getBooleanExtra("cost", false)
//            mStrategy.avoidHeightSpeed = data.getBooleanExtra("avoidHeightSpeed", false)
//            mStrategy.heightSpeed = data.getBooleanExtra("heightSpeed", false)
            onMapLoaded()
        }
    }
}