package com.clusters;

import com.amap.api.maps.model.Marker;

/**
 * @author hfjs
 * @name AmapCarDemo
 * @class marker点击事件
 * @time 2018/10/25 10:20
 */
public interface ClusterItemClickListener {
    /**
     * 方法说明
     * @param marker
     * @param clusterItem
     * @desc 方法说明:
     * @created 2018/10/25 10:50
     */
    void onClick(Marker marker, ClusterItem clusterItem);
}
