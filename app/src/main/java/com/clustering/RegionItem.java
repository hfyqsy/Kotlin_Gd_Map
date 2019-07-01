package com.clustering;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

/**
 * @desc:
 * @author: hfjs
 * @create: 2019-03-07 15:37
 * @change_desc:
 * @change_time:
 **/
public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private Marker marker;
    private String mTitle = "";

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return null;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public RegionItem(LatLng latLng) {
        mLatLng = latLng;
    }

    public RegionItem(LatLng latLng, String title) {
        mLatLng = latLng;
        mTitle = title;
    }


}
