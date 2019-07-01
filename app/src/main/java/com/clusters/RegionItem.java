package com.clusters;

import com.amap.api.maps.model.LatLng;

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private LocationBean mLocationBean;
    private String mTitle;

    public RegionItem(LatLng latLng, LocationBean locationBean) {
        mLatLng = latLng;
        mLocationBean = locationBean;
    }

    public RegionItem(LatLng latLng, String title) {
        mLatLng = latLng;
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    @Override
    public LocationBean getLocationBean() {
        return mLocationBean;
    }

    public String getTitle() {
        return mTitle;
    }
}
