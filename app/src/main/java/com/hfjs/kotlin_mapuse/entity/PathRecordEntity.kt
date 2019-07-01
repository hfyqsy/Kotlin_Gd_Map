package com.hfjs.kotlin_mapuse.entity

import com.amap.api.location.AMapLocation


class PathRecordEntity {
    var mStartPoint: AMapLocation? = null
    var mEndPoint: AMapLocation? = null
    var mPathLinePoints: MutableList<AMapLocation> = ArrayList<AMapLocation>()
    var mDistance: String = ""
    var mDuration: String = ""
    var mAveragespeed: String = ""
    var mDate: String = ""
    var mId: Int = 0

    constructor()

    constructor(
        mStartPoint: AMapLocation, mEndPoint: AMapLocation, mPathLinePoints: MutableList<AMapLocation>,
        mDistance: String, mDuration: String, mAveragespeed: String, mDate: String, mId: Int = 0
    ) {
        this.mStartPoint = mStartPoint
        this.mEndPoint = mEndPoint
        this.mPathLinePoints = mPathLinePoints
        this.mDistance = mDistance
        this.mDuration = mDuration
        this.mAveragespeed = mAveragespeed
        this.mDate = mDate
        this.mId = mId
    }


    override fun toString(): String {
        val record = StringBuilder()
        record.append("recordSize:" + mPathLinePoints.size + ", ")
            .append("distance:" + mDistance + "m, ")
            .append("duration:" + mDuration + "s")
        return record.toString()
    }

    fun addPoint(point: AMapLocation) {
        mPathLinePoints.add(point)
    }

}