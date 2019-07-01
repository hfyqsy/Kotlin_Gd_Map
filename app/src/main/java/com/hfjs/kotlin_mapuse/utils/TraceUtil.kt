package com.hfjs.kotlin_mapuse.utils

import com.amap.api.location.AMapLocation
import com.amap.api.maps.model.LatLng
import com.amap.api.trace.TraceLocation



object TraceUtil {
    /**
     * 将AMapLocation List 转为TraceLocation list
     *
     * @param list
     * @return
     */
    fun parseTraceLocationList(list: List<AMapLocation>?): List<TraceLocation> {
        val traceList = ArrayList<TraceLocation>()
        if (list == null) {
            return traceList
        }
        for (i in list.indices) {
            val location = TraceLocation()
            val amapLocation = list[i]
            location.bearing = amapLocation.bearing
            location.latitude = amapLocation.latitude
            location.longitude = amapLocation.longitude
            location.speed = amapLocation.speed
            location.time = amapLocation.time
            traceList.add(location)
        }
        return traceList
    }

    fun parseTraceLocation(amapLocation: AMapLocation): TraceLocation {
        val location = TraceLocation()
        location.bearing = amapLocation.bearing
        location.latitude = amapLocation.latitude
        location.longitude = amapLocation.longitude
        location.speed = amapLocation.speed
        location.time = amapLocation.time
        return location
    }

    /**
     * 将AMapLocation List 转为LatLng list
     * @param list
     * @return
     */
    fun parseLatLngList(list: List<AMapLocation>?): List<LatLng> {
        val traceList = ArrayList<LatLng>()
        if (list == null) {
            return traceList
        }
        for (i in list.indices) {
            val loc = list[i]
            val lat = loc.latitude
            val lng = loc.longitude
            val latlng = LatLng(lat, lng)
            traceList.add(latlng)
        }
        return traceList
    }

    fun parseLocation(latLonStr: String?): AMapLocation? {
        if (latLonStr == null || latLonStr == "" || latLonStr == "[]") {
            return null
        }
        val loc = latLonStr.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var location: AMapLocation? = null
        if (loc.size == 6) {
            location = AMapLocation(loc[2])
            location.provider = loc[2]
            location.latitude = java.lang.Double.parseDouble(loc[0])
            location.longitude = java.lang.Double.parseDouble(loc[1])
            location.time = java.lang.Long.parseLong(loc[3])
            location.speed = java.lang.Float.parseFloat(loc[4])
            location.bearing = java.lang.Float.parseFloat(loc[5])
        } else if (loc.size == 2) {
            location = AMapLocation("gps")
            location.latitude = java.lang.Double.parseDouble(loc[0])
            location.longitude = java.lang.Double.parseDouble(loc[1])
        }

        return location
    }

    fun parseLocations(latLonStr: String): ArrayList<AMapLocation> {
        val locations = ArrayList<AMapLocation>()
        val latLonStrs = latLonStr.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in latLonStrs.indices) {
            val location = parseLocation(latLonStrs[i])
            if (location != null) {
                locations.add(location)
            }
        }
        return locations
    }
}