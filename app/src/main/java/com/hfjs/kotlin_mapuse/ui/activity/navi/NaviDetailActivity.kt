package com.hfjs.kotlin_mapuse.ui.activity.navi

import com.amap.api.navi.AMapNavi
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseNaviViewActivity


class NaviDetailActivity:BaseNaviViewActivity(R.layout.activity_navi_route) {
    override fun initView() {
        initToolbar(intent.getStringExtra("title"))
        mAMapNavi.setUseInnerVoice(true)
        mAMapNavi.setEmulatorNaviSpeed(60)
        val gps = intent.getBooleanExtra("gps", false)
        if (gps) {
            mAMapNavi.startNavi(AMapNavi.GPSNaviMode)
        } else {
            mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode)
        }
    }
}
