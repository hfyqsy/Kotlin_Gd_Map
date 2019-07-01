package com.hfjs.kotlin_mapuse.ui.activity.trip

import android.app.Activity
import android.os.Bundle
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import com.amap.poisearch.searchmodule.SearchModuleDelegate
import com.amap.api.location.AMapLocation
import com.amap.poisearch.util.CityModel
import com.google.gson.Gson
import android.content.Intent
import com.amap.api.services.core.PoiItem
import com.amap.poisearch.searchmodule.ISearchModule
import kotlinx.android.synthetic.main.activity_trip_host.*


class SetAddressActivity:BaseActivity(R.layout.activity_trip_host) {
    companion object{
        val CURR_CITY_KEY = "curr_city_key"
        val CURR_LOC_KEY = "curr_loc_key"
    }

    private var mFavType = 0
    private var mCurrLoc: AMapLocation? = null


    private lateinit var mSearchModuleDelegate: SearchModuleDelegate
    override fun initView(bundle: Bundle?) {
        mSearchModuleDelegate = SearchModuleDelegate()
        mSearchModuleDelegate.setFavAddressVisible(false)
        mSearchModuleDelegate.bindParentDelegate(mSearchModuleParentDelegate)
        tripRlLayout.addView(mSearchModuleDelegate.getWidget(this))
        mFavType = intent.getIntExtra(ChoosePoiActivity.FAVTYPE_KEY, -1)
    }


    override fun onResume() {
        super.onResume()
        try {
            val currCityStr:String = intent.getStringExtra(CURR_CITY_KEY)
            val cityModel:CityModel =  Gson().fromJson(currCityStr, CityModel::class.java)
            mSearchModuleDelegate.setCity(cityModel)

            mCurrLoc = intent.getParcelableExtra(CURR_LOC_KEY)
            mSearchModuleDelegate.setCurrLoc(mCurrLoc)
        } catch (e: Exception) {
        }


    }

    private val mSearchModuleParentDelegate:ISearchModule.IDelegate.IParentDelegate = object : ISearchModule.IDelegate.IParentDelegate {
        override  fun onChangeCityName() {
            val intent = Intent()
            intent.setClass(this@SetAddressActivity, ChooseCityActivity::class.java)
            intent.putExtra(ChooseCityActivity.CURR_CITY_KEY, mSearchModuleDelegate.currCity.city)
            this@SetAddressActivity.startActivityForResult(intent, 1)
        }

        override   fun onCancel() {
            this@SetAddressActivity.finish()
        }

        override  fun onSetFavHomePoi() {}

        override  fun onSetFavCompPoi() {}

        override    fun onChooseFavHomePoi(poiItem: PoiItem) {}

        override   fun onChooseFavCompPoi(poiItem: PoiItem) {}

        override  fun onSelPoiItem(poiItem: PoiItem) {
            val poiItemStr:String = Gson().toJson(poiItem)
            val resIntent = Intent()
            resIntent.putExtra(ChoosePoiActivity.FAVTYPE_KEY, mFavType)
            resIntent.putExtra(ChoosePoiActivity.POIITEM_OBJECT, poiItemStr)
            this@SetAddressActivity.setResult(Activity.RESULT_OK, resIntent)
            this@SetAddressActivity.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // 表示结果来自城市选择actiivty
        if (requestCode == 1 && requestCode == Activity.RESULT_OK) {
            val currCityStr:String = data!!.getStringExtra(ChooseCityActivity.CURR_CITY_KEY)
            val cityModel:CityModel =  Gson().fromJson(currCityStr, CityModel::class.java)
            mSearchModuleDelegate.setCity(cityModel)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}