package com.hfjs.kotlin_mapuse.utils

import android.graphics.Color
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.navi.model.AMapNaviPath
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.BusPath
import com.hfjs.kotlin_mapuse.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object MapUtil {
    val Kilometer = "\u516c\u91cc"// "公里";
    val Meter = "\u7c73"// "米";
    val ByFoot = "\u6b65\u884c"// "步行";
    val To = "\u53bb\u5f80"// "去往";
    val Station = "\u8f66\u7ad9"// "车站";
    val TargetPlace = "\u76ee\u7684\u5730"// "目的地";
    val StartPlace = "\u51fa\u53d1\u5730"// "出发地";
    val About = "\u5927\u7ea6"// "大约";
    val Direction = "\u65b9\u5411"// "方向";

    val GetOn = "\u4e0a\u8f66"// "上车";
    val GetOff = "\u4e0b\u8f66"// "下车";
    val Zhan = "\u7ad9"// "站";

    val cross = "\u4ea4\u53c9\u8def\u53e3" // 交叉路口
    val type = "\u7c7b\u522b" // 类别
    val address = "\u5730\u5740" // 地址
    val PrevStep = "\u4e0a\u4e00\u6b65"
    val NextStep = "\u4e0b\u4e00\u6b65"
    val Gong = "\u516c\u4ea4"
    val ByBus = "\u4e58\u8f66"
    val Arrive = "\u5230\u8FBE"// 到达
    fun moveMap(mMap: AMap, latLng: LatLng, zoom: Float = 17f) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    fun moveMap(mMap: AMap, bounds: LatLngBounds, zoom: Int = 200) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, zoom))
    }

    fun animateMap(mMap: AMap, latLng: LatLng, zoom: Float = 17f) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    fun animateMap(mMap: AMap, latLng: LatLng, long: Long = 100, zoom: Float = 17f, back: AMap.CancelableCallback) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom), long, back)
    }

    fun getFriendlyTime(s: Int): String {
        var timeDes = ""
        val h = s / 3600
        if (h > 0) {
            timeDes += h.toString() + "小时"
        }
        val min = s % 3600 / 60
        if (min > 0) {
            timeDes += min.toString() + "分"
        }
        return timeDes
    }

    fun getFriendlyTimes(second: Int): String {
        if (second > 3600) {
            val hour = second / 3600
            val miniate = second % 3600 / 60
            return hour.toString() + "小时" + miniate + "分钟"
        }
        if (second >= 60) {
            val miniate = second / 60
            return miniate.toString() + "分钟"
        }
        return second.toString() + "秒"
    }

    fun getFriendlyLength(lenMeter: Int): String {
        if (lenMeter > 10000)
        // 10 km
        {
            val dis = lenMeter / 1000
            return dis.toString() + Kilometer
        }

        if (lenMeter > 1000) {
            val dis = lenMeter.toFloat() / 1000
            val fnum = DecimalFormat("##0.0")
            val dstr = fnum.format(dis.toDouble())
            return dstr + Kilometer
        }

        if (lenMeter > 100) {
            val dis = lenMeter / 50 * 50
            return dis.toString() + Meter
        }

        var dis = lenMeter / 10 * 10
        if (dis == 0) {
            dis = 10
        }

        return dis.toString() + Meter
    }

    private val fnum = DecimalFormat("##0.0")
    fun getFriendlyDistance(m: Int): String {
        val dis = m / 1000f
        return fnum.format(dis) + "公里"
    }

    fun getRouteOverView(path: AMapNaviPath?): Spanned {
        var routeOverView = ""
        if (path == null) {
            Html.fromHtml(routeOverView)
        }

        val cost = path!!.tollCost
        if (cost > 0) {
            routeOverView += "过路费约<font color=\"red\" >$cost</font>元"
        }
        val trafficLightNumber = getTrafficNumber(path)
        if (trafficLightNumber > 0) {
            routeOverView += "红绿灯" + trafficLightNumber + "个"
        }
        return Html.fromHtml(routeOverView)
    }

    private fun getTrafficNumber(path: AMapNaviPath?): Int {
        var trafficLightNumber = 0
        if (path == null) {
            return trafficLightNumber
        }
        val steps = path.steps
        for (step in steps) {
            trafficLightNumber += step.trafficLightNumber
        }
        return trafficLightNumber
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    fun convertToLatLonPoint(latlon: LatLng): LatLonPoint {
        return LatLonPoint(latlon.latitude, latlon.longitude)
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    fun convertToLatLng(latLonPoint: LatLonPoint): LatLng {
        return LatLng(latLonPoint.latitude, latLonPoint.longitude)
    }


    //路径规划方向指示和图片对应
    fun getDriveActionID(actionName: String?): Int {
        if (actionName == null || actionName == "") {
            return R.mipmap.dir3
        }
        if ("左转" == actionName) {
            return R.mipmap.dir2
        }
        if ("右转" == actionName) {
            return R.mipmap.dir1
        }
        if ("向左前方行驶" == actionName || "靠左" == actionName) {
            return R.mipmap.dir6
        }
        if ("向右前方行驶" == actionName || "靠右" == actionName) {
            return R.mipmap.dir5
        }
        if ("向左后方行驶" == actionName || "左转调头" == actionName) {
            return R.mipmap.dir7
        }
        if ("向右后方行驶" == actionName) {
            return R.mipmap.dir8
        }
        if ("直行" == actionName) {
            return R.mipmap.dir3
        }
        return if ("减速行驶" == actionName) {
            R.mipmap.dir4
        } else R.mipmap.dir3
    }

    fun getWalkActionID(actionName: String?): Int {
        if (actionName == null || actionName == "") {
            return R.mipmap.dir13
        }
        if ("左转" == actionName) {
            return R.mipmap.dir2
        }
        if ("右转" == actionName) {
            return R.mipmap.dir1
        }
        if ("向左前方" == actionName || "靠左" == actionName || actionName.contains("向左前方")) {
            return R.mipmap.dir6
        }
        if ("向右前方" == actionName || "靠右" == actionName || actionName.contains("向右前方")) {
            return R.mipmap.dir5
        }
        if ("向左后方" == actionName || actionName.contains("向左后方")) {
            return R.mipmap.dir7
        }
        if ("向右后方" == actionName || actionName.contains("向右后方")) {
            return R.mipmap.dir8
        }
        if ("直行" == actionName) {
            return R.mipmap.dir3
        }
        if ("通过人行横道" == actionName) {
            return R.mipmap.dir9
        }
        if ("通过过街天桥" == actionName) {
            return R.mipmap.dir11
        }
        return if ("通过地下通道" == actionName) {
            R.mipmap.dir10
        } else R.mipmap.dir13

    }

    /**
     * point 和 latlng 转换
     */
    fun convertArrList(shapes: List<LatLonPoint>): ArrayList<LatLng> {
        val lineShapes = ArrayList<LatLng>()
        for (point in shapes) {
            val latLngTemp = convertToLatLng(point)
            lineShapes.add(latLngTemp)
        }
        return lineShapes
    }


    fun getBusPathTitle(busPath: BusPath): String {
        val busSetps = busPath.steps ?: return ("").toString()
        val sb = StringBuffer()
        for (busStep in busSetps) {
            val title = StringBuffer()
            if (busStep.busLines.size > 0) {
                for (busline in busStep.busLines) {
                    if (busline == null) continue
                    val buslineName = getSimpleBusLineName(busline.busLineName)
                    title.append(buslineName)
                    title.append(" / ")
                }
                sb.append(title.substring(0, title.length - 3))
                sb.append(" > ")
            }
            if (busStep.railway != null) {
                val railway = busStep.railway
                sb.append(railway.trip + "(" + railway.departurestop.name + " - " + railway.arrivalstop.name + ")")
                sb.append(" > ")
            }
        }
        return sb.substring(0, sb.length - 3)
    }

    fun getBusPathDes(busPath: BusPath): String {
        val second = busPath.duration
        val time = getFriendlyTime(second.toInt())
        val subDistance = busPath.distance
        val subDis = getFriendlyLength(subDistance.toInt())
        val walkDistance = busPath.walkDistance
        val walkDis = getFriendlyLength(walkDistance.toInt())
        return ("$time | $subDis | 步行$walkDis").toString()
    }

    private fun getSimpleBusLineName(busLineName: String?): String {
        return busLineName?.replace("\\(.*?\\)".toRegex(), "") ?: ""
    }

    /**
     * 根据定位结果返回定位信息的字符串
     * @param location
     * @return
     */
    @Synchronized
    fun getLocationStr(location: AMapLocation?): String? {
        if (null == location) {
            return null
        }
        val sb = StringBuffer()
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.errorCode == 0) {
            sb.append("定位成功" + "\n")
            sb.append("定位类型: " + location.locationType + "\n")
            sb.append("经    度    : " + location.longitude + "\n")
            sb.append("纬    度    : " + location.latitude + "\n")
            sb.append("精    度    : " + location.accuracy + "米" + "\n")
            sb.append("提供者    : " + location.provider + "\n")

            sb.append("海    拔    : " + location.altitude + "米" + "\n")
            sb.append("速    度    : " + location.speed + "米/秒" + "\n")
            sb.append("角    度    : " + location.bearing + "\n")
            if (location.provider.equals(
                    android.location.LocationManager.GPS_PROVIDER, ignoreCase = true
                )
            ) {
                // 以下信息只有提供者是GPS时才会有
                // 获取当前提供定位服务的卫星个数
                sb.append(
                    "星    数    : "
                            + location.satellites + "\n"
                )
            }

            //逆地理信息
            sb.append("国    家    : " + location.country + "\n")
            sb.append("省            : " + location.province + "\n")
            sb.append("市            : " + location.city + "\n")
            sb.append("城市编码 : " + location.cityCode + "\n")
            sb.append("区            : " + location.district + "\n")
            sb.append("区域 码   : " + location.adCode + "\n")
            sb.append("地    址    : " + location.address + "\n")
            sb.append("兴趣点    : " + location.poiName + "\n")
            //定位完成的时间
            sb.append("定位时间: " + formatUTC(location.time, "yyyy-MM-dd HH:mm:ss") + "\n")

        } else {
            //定位失败
            sb.append("定位失败" + "\n")
            sb.append("错误码:" + location.errorCode + "\n")
            sb.append("错误信息:" + location.errorInfo + "\n")
            sb.append("错误描述:" + location.locationDetail + "\n")
        }
        //定位之后的回调时间
        sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n")
        return sb.toString()
    }

    private var sdf: SimpleDateFormat? = null
    @Synchronized
    fun formatUTC(l: Long, strPattern: String?): String {
        var strPattern = strPattern
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss"
        }
        if (sdf == null) {
            try {
                sdf = SimpleDateFormat(strPattern, Locale.CHINA)
            } catch (e: Throwable) {
            }

        } else {
            sdf!!.applyPattern(strPattern)
        }
        return if (sdf == null) "NULL" else sdf!!.format(l)
    }

    //准备测试数据
    fun getTestData(): PolylineOptions {
        val polylineOptions = PolylineOptions()
        polylineOptions
            .add(LatLng(40.005848, 116.38781))
            .add(LatLng(40.005848, 116.38781))
            .add(LatLng(40.006168, 116.387802))
            .add(LatLng(40.00617, 116.388367))
            .add(LatLng(40.00676, 116.388336))
            .add(LatLng(40.006767, 116.388336))
            .add(LatLng(40.006767, 116.388412))
            .add(LatLng(40.006802, 116.389656))
            .add(LatLng(40.006847, 116.391006))
            .add(LatLng(40.006859, 116.391182))
            .add(LatLng(40.006908, 116.39283))
            .add(LatLng(40.006924, 116.393074))
            .add(LatLng(40.006981, 116.3955))
            .add(LatLng(40.006981, 116.3955))
            .add(LatLng(40.007084, 116.395576))
            .add(LatLng(40.007214, 116.39566))
            .add(LatLng(40.007843, 116.396194))
            .add(LatLng(40.00816, 116.396492))
            .add(LatLng(40.008415, 116.396721))
            .add(LatLng(40.008537, 116.396805))
            .add(LatLng(40.008698, 116.396904))
            .add(LatLng(40.008965, 116.397049))
            .add(LatLng(40.009125, 116.397118))
            .add(LatLng(40.009171, 116.397118))
            .add(LatLng(40.009293, 116.397156))
            .add(LatLng(40.009628, 116.397171))
            .add(LatLng(40.009731, 116.397125))
            .add(LatLng(40.009777, 116.397064))
            .add(LatLng(40.009804, 116.397003))
            .add(LatLng(40.009827, 116.396927))
            .add(LatLng(40.009853, 116.396835))
            .add(LatLng(40.009865, 116.396805))
            .add(LatLng(40.009888, 116.39679))
            .add(LatLng(40.009914, 116.396774))
            .add(LatLng(40.009964, 116.396759))
            .add(LatLng(40.010094, 116.396751))
            .add(LatLng(40.010315, 116.396751))
            .add(LatLng(40.010414, 116.396736))
            .add(LatLng(40.010414, 116.396736))
            .add(LatLng(40.010429, 116.39698))
            .add(LatLng(40.010456, 116.397156))
            .add(LatLng(40.010677, 116.39772))
            .add(LatLng(40.010796, 116.398048))
            .add(LatLng(40.010845, 116.398224))
            .add(LatLng(40.010868, 116.398308))
            .add(LatLng(40.010872, 116.398384))
            .add(LatLng(40.010872, 116.39856))
            .add(LatLng(40.010868, 116.398621))
            .add(LatLng(40.010868, 116.398682))
            .add(LatLng(40.010918, 116.398766))
            .add(LatLng(40.010956, 116.398819))
            .add(LatLng(40.011101, 116.398941))
            .add(LatLng(40.01128, 116.399094))
            .add(LatLng(40.011654, 116.399376))
            .add(LatLng(40.011719, 116.399406))
            .add(LatLng(40.011906, 116.399513))
            .add(LatLng(40.012211, 116.399681))
            .add(LatLng(40.012379, 116.399734))
            .add(LatLng(40.012474, 116.39975))
            .add(LatLng(40.012543, 116.39975))
            .add(LatLng(40.012924, 116.399742))
            .add(LatLng(40.013054, 116.399742))
            .add(LatLng(40.013298, 116.399757))
            .add(LatLng(40.013454, 116.399742))
            .add(LatLng(40.013569, 116.399742))
            .add(LatLng(40.013645, 116.39975))
            .add(LatLng(40.014038, 116.39975))
            .add(LatLng(40.014202, 116.399757))
            .add(LatLng(40.014374, 116.399773))
            .add(LatLng(40.014828, 116.399773))
            .add(LatLng(40.015343, 116.399773))
            .add(LatLng(40.015522, 116.399788))
            .add(LatLng(40.015625, 116.399811))
            .add(LatLng(40.015751, 116.399849))
            .add(LatLng(40.015839, 116.399864))
            .add(LatLng(40.015938, 116.399849))
            .add(LatLng(40.016052, 116.399788))
            .add(LatLng(40.016064, 116.399773))
            .add(LatLng(40.016148, 116.399719))
            .add(LatLng(40.016285, 116.399597))
            .add(LatLng(40.016346, 116.399536))
            .add(LatLng(40.01656, 116.399277))
            .add(LatLng(40.016624, 116.399208))
            .add(LatLng(40.016785, 116.399017))
            .add(LatLng(40.016842, 116.398964))
            .add(LatLng(40.01693, 116.398903))
            .add(LatLng(40.017044, 116.398842))
            .add(LatLng(40.017044, 116.398834))
            .add(LatLng(40.017094, 116.398735))
            .add(LatLng(40.017235, 116.398399))
            .add(LatLng(40.0173, 116.398315))
            .add(LatLng(40.017487, 116.398125))
            .add(LatLng(40.017567, 116.398293))
            .add(LatLng(40.017986, 116.398331))
            .add(LatLng(40.018059, 116.398369))
            .add(LatLng(40.018154, 116.398476))
            .add(LatLng(40.018291, 116.398575))
            .add(LatLng(40.018402, 116.398643))
            .add(LatLng(40.018448, 116.398666))
            .add(LatLng(40.018528, 116.398697))
            .add(LatLng(40.018555, 116.398697))
            .add(LatLng(40.018837, 116.398743))
            .add(LatLng(40.018837, 116.398743))
            .add(LatLng(40.018875, 116.398682))
            .add(LatLng(40.018929, 116.398628))
            .add(LatLng(40.018944, 116.398613))
            .add(LatLng(40.01902, 116.398613))
            .add(LatLng(40.019131, 116.398621))
            .add(LatLng(40.019154, 116.398621))
            .add(LatLng(40.019203, 116.398613))
            .add(LatLng(40.019352, 116.398422))
            .add(LatLng(40.019405, 116.398331))
            .add(LatLng(40.019436, 116.39827))
            .add(LatLng(40.01944, 116.398247))
            .add(LatLng(40.019444, 116.398224))
            .add(LatLng(40.019444, 116.39814))
            .add(LatLng(40.01944, 116.398109))
            .add(LatLng(40.019417, 116.398003))
            .add(LatLng(40.019402, 116.397881))
            .add(LatLng(40.019402, 116.39769))
            .add(LatLng(40.019413, 116.397629))
            .add(LatLng(40.019447, 116.39756))
            .add(LatLng(40.019478, 116.39753))
            .add(LatLng(40.019661, 116.397362))
            .add(LatLng(40.019733, 116.397278))
            .add(LatLng(40.019787, 116.397194))
            .add(LatLng(40.019814, 116.397156))
            .add(LatLng(40.019825, 116.397102))
            .add(LatLng(40.019836, 116.397034))
            .add(LatLng(40.019836, 116.396965))
            .add(LatLng(40.01981, 116.396721))
            .add(LatLng(40.019802, 116.396614))
            .add(LatLng(40.019802, 116.396553))
            .add(LatLng(40.019836, 116.396461))
            .add(LatLng(40.019882, 116.396362))
            .add(LatLng(40.019913, 116.396286))
            .add(LatLng(40.019928, 116.396217))
            .add(LatLng(40.019932, 116.396164))
            .add(LatLng(40.019932, 116.396065))
            .add(LatLng(40.019917, 116.395981))
            .add(LatLng(40.019878, 116.395744))
            .add(LatLng(40.01984, 116.395592))
            .add(LatLng(40.019836, 116.395592))
            .add(LatLng(40.019859, 116.395523))
            .add(LatLng(40.019886, 116.395424))
            .add(LatLng(40.019913, 116.39537))
            .add(LatLng(40.019958, 116.395309))
            .add(LatLng(40.019997, 116.395279))
            .add(LatLng(40.020077, 116.39521))
            .add(LatLng(40.02023, 116.395119))
            .add(LatLng(40.020237, 116.395119))
            .add(LatLng(40.020164, 116.394989))
            .add(LatLng(40.020142, 116.394913))
            .add(LatLng(40.020134, 116.394852))
            .add(LatLng(40.020126, 116.3946))
            .add(LatLng(40.020081, 116.394478))
            .add(LatLng(40.020073, 116.394447))
            .add(LatLng(40.020073, 116.394417))
            .add(LatLng(40.020081, 116.394394))
            .add(LatLng(40.020111, 116.394348))
            .add(LatLng(40.020199, 116.394287))
            .add(LatLng(40.020226, 116.394241))
            .add(LatLng(40.020229, 116.39418))
            .add(LatLng(40.020226, 116.394096))
            .add(LatLng(40.020222, 116.394096))
            .add(LatLng(40.020321, 116.394081))
            .add(LatLng(40.020401, 116.394028))
            .add(LatLng(40.020451, 116.393959))
            .add(LatLng(40.020489, 116.393875))
            .add(LatLng(40.020508, 116.393768))
            .add(LatLng(40.020538, 116.3936))
            .add(LatLng(40.020584, 116.393509))
            .add(LatLng(40.020721, 116.393349))
            .add(LatLng(40.02108, 116.393005))
            .add(LatLng(40.021355, 116.392815))
            .add(LatLng(40.021381, 116.392784))
            .add(LatLng(40.021389, 116.392761))
            .add(LatLng(40.0214, 116.392708))
            .add(LatLng(40.021416, 116.392624))
            .add(LatLng(40.021416, 116.392624))
            .add(LatLng(40.021461, 116.39257))
            .add(LatLng(40.021484, 116.392555))
            .add(LatLng(40.021622, 116.392479))
            .add(LatLng(40.02174, 116.392365))
            .add(LatLng(40.021896, 116.392189))

        val colors = ArrayList<Int>()
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.GREEN)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.YELLOW)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        colors.add(Color.RED)
        polylineOptions.width(10f)
        polylineOptions.colorValues(colors)
        return polylineOptions
    }

    public val coords = doubleArrayOf(
        116.3499049793749,
        39.97617053371078,
        116.34978804908442,
        39.97619854213431,
        116.349674596623,
        39.97623045687959,
        116.34955525200917,
        39.97626931100656,
        116.34943728748914,
        39.976285626595036,
        116.34930864705592,
        39.97628129172198,
        116.34918981582413,
        39.976260803938594,
        116.34906721558868,
        39.97623535890678,
        116.34895185151584,
        39.976214717128855,
        116.34886935936889,
        39.976280148755315,
        116.34873954611332,
        39.97628182112874,
        116.34860763527448,
        39.97626038855863,
        116.3484658907622,
        39.976306080391836,
        116.34834585430347,
        39.976358252119745,
        116.34831166130878,
        39.97645709321835,
        116.34827643560175,
        39.97655231226543,
        116.34824186261169,
        39.976658372925556,
        116.34825080406188,
        39.9767570732376,
        116.34825631960626,
        39.976869087779995,
        116.34822111635201,
        39.97698451764595,
        116.34822901510276,
        39.977079745909876,
        116.34822234337618,
        39.97718701787645,
        116.34821627457707,
        39.97730766147824,
        116.34820593515043,
        39.977417746816776,
        116.34821013897107,
        39.97753930933358,
        116.34821304891533,
        39.977652209132174,
        116.34820923399242,
        39.977764016531076,
        116.3482045955917,
        39.97786190186833,
        116.34822159449203,
        39.977958856930286,
        116.3482256370537,
        39.97807288885813,
        116.3482098441266,
        39.978170063673524,
        116.34819564465377,
        39.978266951404066,
        116.34820541974412,
        39.978380693859116,
        116.34819672351216,
        39.97848741209275,
        116.34816588867105,
        39.978593409607825,
        116.34818489339459,
        39.97870216883567,
        116.34818473446943,
        39.978797222300166,
        116.34817728972234,
        39.978893492422685,
        116.34816491505472,
        39.978997133775266,
        116.34815408537773,
        39.97911413849568,
        116.34812908154862,
        39.97920553614499,
        116.34809495907906,
        39.979308267469264,
        116.34805113358091,
        39.97939658036473,
        116.3480310509613,
        39.979491697188685,
        116.3480082124968,
        39.979588529006875,
        116.34799530586834,
        39.979685789111635,
        116.34798818413954,
        39.979801430587926,
        116.3479996420353,
        39.97990758587515,
        116.34798697544538,
        39.980000796262615,
        116.3479912988137,
        39.980116318796085,
        116.34799204219203,
        39.98021407403913,
        116.34798535084123,
        39.980325006125696,
        116.34797702460183,
        39.98042511477518,
        116.34796288754136,
        39.98054129336908,
        116.34797509821901,
        39.980656820423505,
        116.34793922017285,
        39.98074576792626,
        116.34792586413015,
        39.98085620772756,
        116.3478962642899,
        39.98098214824056,
        116.34782449883967,
        39.98108306010269,
        116.34774758827285,
        39.98115277119176,
        116.34761476652932,
        39.98115430642997,
        116.34749135408349,
        39.98114590845294,
        116.34734772765582,
        39.98114337322547,
        116.34722082902628,
        39.98115066909245,
        116.34708205250223,
        39.98114532232906,
        116.346963237696,
        39.98112245161927,
        116.34681500222743,
        39.981136637759604,
        116.34669622104072,
        39.981146248090866,
        116.34658043260109,
        39.98112495260716,
        116.34643721418927,
        39.9811107163792,
        116.34631638374302,
        39.981085081075676,
        116.34614782996252,
        39.98108046779486,
        116.3460256053666,
        39.981049089345206,
        116.34588814050122,
        39.98104839362087,
        116.34575119741586,
        39.9810544889668,
        116.34562885420186,
        39.981040940565734,
        116.34549232235582,
        39.98105271658809,
        116.34537348820508,
        39.981052294975264,
        116.3453513775533,
        39.980956549928244
    )

}