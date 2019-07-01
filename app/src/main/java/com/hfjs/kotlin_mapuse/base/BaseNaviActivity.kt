package com.hfjs.kotlin_mapuse.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import com.amap.api.maps.AMap
import com.amap.api.maps.SupportMapFragment
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.model.*
import com.autonavi.tbt.TrafficFacilityInfo
import kotlinx.android.synthetic.main.include_map_view.*

open class BaseNaviActivity(@LayoutRes override val layoutRes: Int) : BaseActivity(layoutRes), AMapNaviListener {


    protected lateinit var aMap: AMap
    protected lateinit var mAMapNavi: AMapNavi

    override fun initView(bundle: Bundle?) {
        mMapView.onCreate(bundle)
        aMap = mMapView.map
        mAMapNavi = AMapNavi.getInstance(applicationContext);
        mAMapNavi.addAMapNaviListener(this);

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
        mAMapNavi.destroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }


    override fun onNaviInfoUpdate(p0: NaviInfo?) {

    }

    override fun onCalculateRouteSuccess(p0: IntArray?) {

    }

    override fun onCalculateRouteSuccess(p0: AMapCalcRouteResult?) {

    }

    override fun onCalculateRouteFailure(p0: Int) {

    }

    override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {

    }

    override fun onServiceAreaUpdate(p0: Array<out AMapServiceAreaInfo>?) {

    }

    override fun onEndEmulatorNavi() {

    }

    override fun onArrivedWayPoint(p0: Int) {

    }

    override fun onArriveDestination() {

    }

    override fun onPlayRing(p0: Int) {

    }

    override fun onTrafficStatusUpdate() {

    }

    override fun onGpsOpenStatus(p0: Boolean) {

    }

    override fun updateAimlessModeCongestionInfo(p0: AimLessModeCongestionInfo?) {

    }

    override fun showCross(p0: AMapNaviCross?) {

    }

    override fun onGetNavigationText(p0: Int, p1: String?) {

    }

    override fun onGetNavigationText(p0: String?) {

    }

    override fun updateAimlessModeStatistics(p0: AimLessModeStat?) {

    }

    override fun hideCross() {

    }

    override fun onInitNaviFailure() {

    }

    override fun onInitNaviSuccess() {

    }

    override fun onReCalculateRouteForTrafficJam() {

    }

    override fun updateIntervalCameraInfo(p0: AMapNaviCameraInfo?, p1: AMapNaviCameraInfo?, p2: Int) {

    }

    override fun hideLaneInfo() {

    }

    override fun onNaviInfoUpdated(p0: AMapNaviInfo?) {

    }

    override fun showModeCross(p0: AMapModelCross?) {

    }

    override fun updateCameraInfo(p0: Array<out AMapNaviCameraInfo>?) {

    }

    override fun hideModeCross() {

    }

    override fun onLocationChange(p0: AMapNaviLocation?) {

    }

    override fun onReCalculateRouteForYaw() {

    }

    override fun onStartNavi(p0: Int) {

    }

    override fun notifyParallelRoad(p0: Int) {

    }

    override fun OnUpdateTrafficFacility(p0: AMapNaviTrafficFacilityInfo?) {

    }

    override fun OnUpdateTrafficFacility(p0: Array<out AMapNaviTrafficFacilityInfo>?) {

    }

    override fun OnUpdateTrafficFacility(p0: TrafficFacilityInfo?) {

    }

    override fun onNaviRouteNotify(p0: AMapNaviRouteNotifyData?) {

    }

    override fun showLaneInfo(p0: Array<out AMapLaneInfo>?, p1: ByteArray?, p2: ByteArray?) {

    }

    override fun showLaneInfo(p0: AMapLaneInfo?) {

    }

}