package com.hfjs.kotlin_mapuse.ui.activity.trail

import android.content.res.AssetManager
import android.graphics.Color
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.hfjs.kotlin_mapuse.R
import com.hfjs.kotlin_mapuse.base.BaseMapActivity
import com.hfjs.kotlin_mapuse.utils.MapUtil
import com.hfjs.kotlin_mapuse.utils.PathSmoothUtil
import kotlinx.android.synthetic.main.activity_smooth.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import com.amap.api.maps.model.LatLngBounds


class SmoothActivity : BaseMapActivity(R.layout.activity_smooth) {
    private lateinit var mSmooth: PathSmoothUtil
    private var mOriginList: List<LatLng>? = null
    private lateinit var mOrigin: Polyline
    private lateinit var mKalMan: Polyline
    /**
     * 初始化
     */
    override fun initView() {
        mSmooth = PathSmoothUtil
        mSmooth.setIntensity(4)

    }

    /**
     * 初始化数据
     */
    override fun initData() {
        addLocalData()
    }

    /**
     * 设置监听事件
     */
    override fun setListener() {
        rdbUnOptimize.setOnClickListener {
            mKalMan.isVisible = false
            mOrigin.isVisible = true;
        }
        rdbOptimize.setOnClickListener {
            mKalMan.isVisible = true
            mOrigin.isVisible = false
        }
    }

    /**
     * 添加本地数据
     */
    private fun addLocalData() {
        mOriginList = getParseData(assets, "AMapTrace2.csv")
        if (mOriginList!!.isNotEmpty()) {
            mOrigin = aMap.addPolyline(PolylineOptions().addAll(mOriginList).color(Color.GREEN))
            MapUtil.moveMap(aMap, getBounds(mOriginList))
        }
        pathOptimize(mOriginList!!)
    }

    //轨迹平滑优化
    private fun pathOptimize(originList: List<LatLng>) {
        val pathOptimize = mSmooth.pathOptimize(originList)
        mKalMan = aMap.addPolyline(PolylineOptions().addAll(pathOptimize).color(Color.parseColor("#FFC125")))
        mKalMan.isVisible = false
    }

    /**
     * 聚合Bound
     */
    private fun getBounds(pointlist: List<LatLng>?): LatLngBounds {
        val b = LatLngBounds.builder()
        if (pointlist == null) {
            return b.build()
        }
        for (i in pointlist.indices) {
            b.include(pointlist[i])
        }
        return b.build()

    }

    /**
     * 解析Assets数据
     */
    private fun getParseData(mAssetManager: AssetManager, filePath: String): List<LatLng> {
        val locLists = ArrayList<LatLng>()
        var input: InputStream? = null
        var inputReader: InputStreamReader? = null
        var bufReader: BufferedReader? = null
        try {
            input = mAssetManager.open(filePath)
            inputReader = InputStreamReader(input)
            bufReader = BufferedReader(inputReader)
            var line = ""

            do {
                line = bufReader.readLine()
                if (line != null) {
                    var strArray: Array<String>? = null
                    strArray = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val newpoint =
                        LatLng(java.lang.Double.parseDouble(strArray[1]), java.lang.Double.parseDouble(strArray[2]))
                    if (locLists.size == 0 || newpoint.toString() !== locLists[locLists.size - 1].toString()) {
                        locLists.add(newpoint)
                    }
                }
            } while (true)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (bufReader != null) {
                    bufReader.close()
                    bufReader = null
                }
                if (inputReader != null) {
                    inputReader.close()
                    inputReader = null
                }
                if (input != null) {
                    input.close()
                    input = null
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return locLists
    }
}