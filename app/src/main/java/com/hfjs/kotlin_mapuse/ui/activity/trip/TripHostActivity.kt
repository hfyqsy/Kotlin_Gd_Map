package com.hfjs.kotlin_mapuse.ui.activity.trip

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItem
import com.amap.poisearch.searchmodule.ISearchModule.IDelegate.DEST_POI_TYPE
import com.amap.poisearch.searchmodule.ISearchModule.IDelegate.START_POI_TYPE
import com.amap.poisearch.util.CityModel
import com.amap.tripmodule.ITripHostModule
import com.amap.tripmodule.TripHostModuleDelegate
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.hfjs.kotlin_mapuse.utils.toast
import kotlinx.android.synthetic.main.activity_trip_host.*


class TripHostActivity : BaseActivity(R.layout.activity_trip_host) {
    private val REQUEST_CHOOSE_CITY = 1
    private val REQUEST_CHOOSE_START_POI = 2
    private val REQUEST_CHOOSE_DEST_POI = 3
    private val MIN_START_DEST_DISTANCE = 1000

    private var mDestPoi: PoiItem? = null
    private var mStartPoi: PoiItem? = null
    private lateinit var mTripHosDelegate: TripHostModuleDelegate
    override fun initView(bundle: Bundle?) {
        mTripHosDelegate = TripHostModuleDelegate()
        mTripHosDelegate.bindParentDelegate(mParentDelegate)
        tripRlLayout.addView(mTripHosDelegate.getWidget(this))
        mTripHosDelegate.onCreate(bundle)
    }

    override fun onResume() {
        super.onResume()
        mTripHosDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mTripHosDelegate.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTripHosDelegate.onDestroy()
    }

    val mParentDelegate = object : ITripHostModule.IParentDelegate {
        override fun onStartPoiChange(poiItem: PoiItem?) {
            if (poiItem == null) {
                return;
            }

            mTripHosDelegate.setStartLocation(poiItem.title);
            mStartPoi = poiItem;
        }

        override fun onMsgClick() {
            toast("msg click")
        }

        override fun onIconClick() {
            toast("icon click")
        }

        override fun onBackToInputMode() {
            onBackToInputMode()
        }

        override fun onStartCall() {
            toast("Start Call")
        }

        override fun onChooseCity() {
            val intent = Intent(this@TripHostActivity, ChooseCityActivity::class.java)
            intent.putExtra(ChooseCityActivity.CURR_CITY_KEY, mTripHosDelegate.currCity.city)
            startActivityForResult(intent, REQUEST_CHOOSE_CITY)
            this@TripHostActivity.overridePendingTransition(R.anim.slide_in_up, 0)
        }

        override fun onChooseDestPoi() {
            val intent = Intent(this@TripHostActivity, ChoosePoiActivity::class.java)
            intent.putExtra(ChoosePoiActivity.POI_TYPE_KEY, DEST_POI_TYPE)
            intent.putExtra(ChoosePoiActivity.CITY_KEY, mTripHosDelegate.currCity)
            startActivityForResult(intent, REQUEST_CHOOSE_DEST_POI)
            this@TripHostActivity.overridePendingTransition(R.anim.slide_in_up, 0)
        }

        override fun onChooseStartPoi() {
            val intent = Intent(this@TripHostActivity, ChoosePoiActivity::class.java)
            intent.putExtra(ChoosePoiActivity.POI_TYPE_KEY, START_POI_TYPE)
            intent.putExtra(ChoosePoiActivity.CITY_KEY, mTripHosDelegate.currCity)
            startActivityForResult(intent, REQUEST_CHOOSE_START_POI)
            this@TripHostActivity.overridePendingTransition(R.anim.slide_in_up, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        if (REQUEST_CHOOSE_CITY === requestCode) {
            if (resultCode === Activity.RESULT_OK) {
                val cityModel: CityModel = data.getParcelableExtra(ChooseCityActivity.CURR_CITY_KEY)
                mTripHosDelegate.currCity = cityModel
            }
        }

        if (REQUEST_CHOOSE_DEST_POI === requestCode) {
            if (resultCode === Activity.RESULT_OK) {
                try {
                    val poiItem:PoiItem = data.getParcelableExtra(ChoosePoiActivity.POIITEM_OBJECT)

                    val res = FloatArray(1)
                    Location.distanceBetween(
                        poiItem.latLonPoint.latitude,
                        poiItem.latLonPoint.longitude, mStartPoi!!.latLonPoint.latitude,
                        mStartPoi!!.latLonPoint.longitude, res
                    )

                    if (res[0] <= MIN_START_DEST_DISTANCE) {
                        toast("距离过近，请重新选择目的地")
                        return
                    }

                    mDestPoi = poiItem
                    mTripHosDelegate.setDestLocation(poiItem.title)
                    if (mDestPoi != null && mStartPoi != null) onShowPoiRes()
                } catch (e: Exception) {
                    toast("请选择正确的目的地")
                }
            }
        }

        if (REQUEST_CHOOSE_START_POI === requestCode) {
            if (resultCode === Activity.RESULT_OK) {
                try {
                    val poiItem:PoiItem = data.getParcelableExtra(ChoosePoiActivity.POIITEM_OBJECT)
                    mStartPoi = poiItem
                    mTripHosDelegate.setStartLocation(poiItem.title)
                    mTripHosDelegate.moveCameraPosTo(
                        LatLng(
                            poiItem.latLonPoint.latitude,
                            poiItem.latLonPoint.longitude
                        )
                    )
                } catch (e: Exception) {
                    toast("请选择正确的目的地")
                }

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onShowPoiRes() {

        val startLL = LatLng(
            mStartPoi!!.latLonPoint.latitude,
            mStartPoi!!.latLonPoint.longitude
        )
        val destLL = LatLng(
            mDestPoi!!.latLonPoint.latitude,
            mDestPoi!!.latLonPoint.longitude
        )


        mTripHosDelegate.showPoiRes(startLL, destLL)
    }

    override fun onBackPressed() {
        if (mTripHosDelegate.mode === ITripHostModule.IDelegate.SHOW_RES_MODE) {
            onBackToInputMode()
            return
        }

        super.onBackPressed()
    }

    /**
     * 在切换为输入模式后，设置模式，触发TripHostDelegate重置
     */
    private fun onBackToInputMode() {
        mTripHosDelegate.setMode(ITripHostModule.IDelegate.INPUT_MODE, mStartPoi)
        mDestPoi = null
    }
}