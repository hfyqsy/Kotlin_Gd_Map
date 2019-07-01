package com.hfjs.kotlin_mapuse.ui.activity.trip

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.amap.poisearch.searchmodule.CityChooseDelegate
import com.amap.poisearch.searchmodule.ICityChooseModule
import com.amap.poisearch.util.CityModel
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseActivity
import kotlinx.android.synthetic.main.activity_trip_host.*

class ChooseCityActivity : BaseActivity(R.layout.activity_trip_host) {
    private lateinit var mCityChooseDelegate: CityChooseDelegate

    companion object {
        val CURR_CITY_KEY = "curr_city_key"
    }

    override fun initView(bundle: Bundle?) {
        mCityChooseDelegate = CityChooseDelegate()
        mCityChooseDelegate.bindParentDelegate(mCityChooseParentDelegate)
        tripRlLayout.addView(mCityChooseDelegate.getWidget(this))
    }

    private val mCityChooseParentDelegate: ICityChooseModule.IParentDelegate =
        object : ICityChooseModule.IParentDelegate {
            override fun onChooseCity(city: CityModel?) {
                val intent = Intent()
                intent.putExtra(CURR_CITY_KEY, city)
                this@ChooseCityActivity.setResult(Activity.RESULT_OK, intent)
                this@ChooseCityActivity.finish()
                this@ChooseCityActivity.overridePendingTransition(0, R.anim.slide_out_down)
            }

            override fun onCancel() {
                finish();
                overridePendingTransition(0, R.anim.slide_out_down);
            }

        }
}