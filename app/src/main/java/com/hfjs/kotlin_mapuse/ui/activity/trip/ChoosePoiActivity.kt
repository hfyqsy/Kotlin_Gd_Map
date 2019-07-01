package com.hfjs.kotlin_mapuse.ui.activity.trip

import android.app.Activity
import android.os.Bundle
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.amap.poisearch.searchmodule.SearchModuleDelegate
import com.amap.poisearch.searchmodule.ISearchModule.IDelegate.START_POI_TYPE
import com.amap.poisearch.searchmodule.ISearchModule.IDelegate.IParentDelegate;
import com.amap.poisearch.util.CityModel
import com.amap.poisearch.util.PoiItemDBHelper
import android.content.Intent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.services.core.PoiItem
import com.hfjs.kotlin_mapuse.utils.toast
import com.amap.poisearch.util.FavAddressUtil
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_trip_host.*


class ChoosePoiActivity : BaseActivity(R.layout.activity_trip_host) {
    private var MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE = 1
    private var MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE = 2

    companion object {
        val POI_TYPE_KEY = "poi_type_key"
        val CITY_KEY = "city_key"
        val FAVTYPE_KEY = "favtype"
        val POIITEM_OBJECT = "poiitem_object"
    }

    private var mPoiType: Int = 0
    private var mCityModel: CityModel? = null
    private var mCurrLoc: AMapLocation? = null
    private lateinit var mSearchModuleDelegate: SearchModuleDelegate
    private lateinit var mLocationClient: AMapLocationClient

    override fun initView(bundle: Bundle?) {
        if (intent != null) {
            mPoiType = intent.getIntExtra(POI_TYPE_KEY, START_POI_TYPE)
            mCityModel = intent.getParcelableExtra(CITY_KEY)
        }

        mSearchModuleDelegate = SearchModuleDelegate()
        mSearchModuleDelegate.setPoiType(mPoiType)
        mSearchModuleDelegate.setCity(mCityModel)
        mSearchModuleDelegate.bindParentDelegate(mParentDelegate)
        tripRlLayout.addView(mSearchModuleDelegate.getWidget(this))
    }

    private fun initLocation() {
        mLocationClient = AMapLocationClient(this)
        mLocationClient.setLocationListener {
            if (mCurrLoc == null) {
                mCurrLoc = it
                mSearchModuleDelegate.setCurrLoc(mCurrLoc)
            }
        }
        mLocationClient.startLocation()
    }

    override fun onPause() {
        mCurrLoc = null
        super.onPause()
    }

    private val mParentDelegate = object : IParentDelegate {
        override fun onChangeCityName() {
            toast("选择城市")
            val intent = Intent()
            intent.setClass(this@ChoosePoiActivity, ChooseCityActivity::class.java)
            intent.putExtra(ChooseCityActivity.CURR_CITY_KEY, mSearchModuleDelegate.currCity.city)
            this@ChoosePoiActivity.startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE)
        }

        override fun onCancel() {
            this@ChoosePoiActivity.finish()
            this@ChoosePoiActivity.overridePendingTransition(0, R.anim.slide_out_down)
        }

        private fun toSetFavAddressActivity(type: Int) {
            val intent = Intent()
            intent.setClass(this@ChoosePoiActivity, SetAddressActivity::class.java)
            intent.putExtra(FAVTYPE_KEY, type)
            val gson = Gson()
            intent.putExtra(SetAddressActivity.CURR_CITY_KEY, gson.toJson(mSearchModuleDelegate.currCity))
            intent.putExtra(SetAddressActivity.CURR_LOC_KEY, mCurrLoc)

            startActivityForResult(intent, MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE)
        }

        override fun onSetFavHomePoi() {
            toast("设置家的地址")
            toSetFavAddressActivity(0)
        }

        override fun onSetFavCompPoi() {
            toast("设置公司地址")
            toSetFavAddressActivity(1)
        }

        override fun onChooseFavHomePoi(poiItem: PoiItem) {
            val intent = Intent()
            intent.putExtra(POI_TYPE_KEY, mPoiType)
            intent.putExtra(POIITEM_OBJECT, poiItem)
            setResult(Activity.RESULT_OK, intent)
            this@ChoosePoiActivity.finish()
            this@ChoosePoiActivity.overridePendingTransition(0, R.anim.slide_out_down)
        }

        override fun onChooseFavCompPoi(poiItem: PoiItem) {
            val intent = Intent()
            intent.putExtra(POI_TYPE_KEY, mPoiType)
            intent.putExtra(POIITEM_OBJECT, poiItem)
            setResult(Activity.RESULT_OK, intent)
            this@ChoosePoiActivity.finish()
            this@ChoosePoiActivity.overridePendingTransition(0, R.anim.slide_out_down)
        }

        override fun onSelPoiItem(poiItem: PoiItem) {
            saveToCache(poiItem)
            val intent = Intent()
            intent.putExtra(POI_TYPE_KEY, mPoiType)
            intent.putExtra(POIITEM_OBJECT, poiItem)
            setResult(Activity.RESULT_OK, intent)
            this@ChoosePoiActivity.finish()
            this@ChoosePoiActivity.overridePendingTransition(0, R.anim.slide_out_down)
        }

        private fun saveToCache(poiItem: PoiItem) {
            PoiItemDBHelper.getInstance().savePoiItem(this@ChoosePoiActivity, poiItem)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (MAIN_ACTIVITY_REQUEST_FAV_ADDRESS_CODE === requestCode && resultCode == Activity.RESULT_OK) {
            val poiitemStr = data!!.getStringExtra(POIITEM_OBJECT)
            val favType = data.getIntExtra(FAVTYPE_KEY, -1)
            val poiItem = Gson().fromJson(poiitemStr, PoiItem::class.java)

            if (favType == 0) {
                FavAddressUtil.saveFavHomePoi(this, poiItem)
                mSearchModuleDelegate.setFavHomePoi(poiItem)
            } else if (favType == 1) {
                FavAddressUtil.saveFavCompPoi(this, poiItem)
                mSearchModuleDelegate.setFavCompPoi(poiItem)
            }
        }

        if (MAIN_ACTIVITY_REQUEST_CHOOSE_CITY_ADDRESS_CODE === requestCode && resultCode == Activity.RESULT_OK) {
            val cityModel = data!!.getParcelableExtra<CityModel>(ChooseCityActivity.CURR_CITY_KEY)
            mSearchModuleDelegate.setCity(cityModel)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}