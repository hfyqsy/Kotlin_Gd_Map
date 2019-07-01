package com.hfjs.kotlin_mapuse.entity

import com.amap.api.services.route.BusStep

class BusStepEntity(step: BusStep?) : BusStep() {

    var isWalk = false
    var isBus = false
    var isRailway = false
    var isTaxi = false
    var isStart = false
    var isEnd = false
    var arrowExpend = false

    init {
        if (step != null) {
            this.busLine = step.busLine
            this.walk = step.walk
            this.railway = step.railway
            this.taxi = step.taxi
        }
    }


}
