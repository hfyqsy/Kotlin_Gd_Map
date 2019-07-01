package com.hfjs.kotlin_mapuse.ui.activity.route

import android.os.Bundle
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.AMapNaviViewListener
import com.amap.api.navi.model.*
import com.autonavi.tbt.TrafficFacilityInfo
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import kotlinx.android.synthetic.main.activity_route_navi.*


class RouteNaviActivity : BaseActivity(R.layout.activity_route_navi), AMapNaviListener, AMapNaviViewListener {
    private lateinit var mAMapNavi: AMapNavi
    override fun initView(bundle: Bundle?) {
        super.initView(bundle)
        initToolbar(intent.getIntExtra("title", 0))
        mAMapNavi = AMapNavi.getInstance(applicationContext);
        mAMapNavi.addAMapNaviListener(this);
        naviView.onCreate(bundle)
        naviView.setAMapNaviViewListener(this)
        mAMapNavi.setUseInnerVoice(true)
        mAMapNavi.setEmulatorNaviSpeed(60)

        val gps = intent.getBooleanExtra("gps", false)
        if (gps) {
            mAMapNavi.startNavi(AMapNavi.GPSNaviMode)
        } else {
            mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode)
        }

    }

    override fun onResume() {
        super.onResume()
        naviView.onResume()
    }

    override fun onPause() {
        super.onPause()
        naviView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        naviView.onDestroy()
        mAMapNavi.destroy()
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


    override fun onNaviTurnClick() {

    }

    override fun onScanViewButtonClick() {

    }

    override fun onLockMap(p0: Boolean) {

    }

    override fun onMapTypeChanged(p0: Int) {

    }

    override fun onNaviCancel() {
         finish()
    }

    override fun onNaviViewLoaded() {

    }

    override fun onNaviBackClick(): Boolean {
        return false
    }

    override fun onNaviMapMode(p0: Int) {

    }

    override fun onNextRoadClick() {

    }

    override fun onNaviViewShowMode(p0: Int) {

    }

    override fun onNaviSetting() {

    }


}