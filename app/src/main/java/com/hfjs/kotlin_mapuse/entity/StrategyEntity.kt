package com.hfjs.kotlin_mapuse.entity

import java.io.Serializable

/**
 * 方法:
 *   int strategy = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
 * 参数:
 * @congestion 躲避拥堵
 * @avoidhightspeed 不走高速
 * @cost 避免收费
 * @hightspeed 高速优先
 * @multipleroute 多路径
 *
 * 说明:
 *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
 * 注意:
 *      不走高速与高速优先不能同时为true
 *      高速优先与避免收费不能同时为true
 */
class StrategyEntity (
    var congestion: Boolean,
    var  avoidHeightSpeed: Boolean,
    var  cost: Boolean,
    var  heightSpeed: Boolean,
    var  multipleRoute: Boolean
) :Serializable
