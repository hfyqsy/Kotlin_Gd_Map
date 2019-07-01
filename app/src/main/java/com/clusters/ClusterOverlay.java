package com.clusters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.*;
import com.amap.api.maps.model.animation.AlphaAnimation;
import com.amap.api.maps.model.animation.Animation;
import com.hfjs.kotlin_mapuse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay implements AMap.OnCameraChangeListener, AMap.OnMarkerClickListener {
    private AMap mAMap;
    private Context mContext;
    private List<ClusterItem> mClusterItems;
    private List<Cluster> mClusters;
    private ClusterClickListener mClusterClickListener;
    private ClusterItemClickListener mClusterItemClickListener;
    private ClusterRender mClusterRender;//提供bitmap
    private List<Marker> mAddMarkers = new ArrayList<Marker>();
    private  HashMap<Integer, BitmapDescriptor> mLruCache;
    private HandlerThread mMarkerHandlerThread = new HandlerThread("addMarker");
    private HandlerThread mSignClusterThread = new HandlerThread("calculateCluster");
    private Handler mMarkerhandler;
    private Handler mSignClusterHandler;//单个添加

    private double mClusterDistance;//聚合距离
    private int mClusterSize;//聚合点数量
    private float mPXInMeters;//聚合距离
    private boolean mIsCanceled = false;
    private int clusterSize;//记录聚合
    private float zoom = 0;//缩放等级

    private LatLng point;
    private Marker popupMarker;

    private HashMap<String, BitmapDescriptor> mViewLruCache;

    /**
     * 构造函数
     *
     * @param amap
     * @param clusterSize 聚合范围的大小（指点像素单位距离内的点会聚合到一个点显示）
     * @param context
     */
    public ClusterOverlay(AMap amap, int clusterSize, Context context) {
        this(amap, null, clusterSize, context);


    }

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(AMap amap, List<ClusterItem> clusterItems, int clusterSize, Context context) {
//默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache =new HashMap<>();/*= new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
             if (oldValue!=null)  oldValue.getBitmap().recycle();
            }
        };*/
        mViewLruCache =new HashMap<>();
    /*new LruCache<String, BitmapDescriptor>(80) {
            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };*/
        if (clusterItems != null) {
            mClusterItems = clusterItems;
        } else {
            mClusterItems = new ArrayList<ClusterItem>();
        }
        mContext = context;
        mClusters = new ArrayList<Cluster>();
        this.mAMap = amap;
        this.clusterSize = clusterSize;
        mClusterSize = clusterSize;
        mPXInMeters = mAMap.getScalePerPixel();
        mClusterDistance = mPXInMeters * mClusterSize;
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        initThreadHandler();
        assignClusters();
    }


    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterItemClickListener
     */
    public void setOnClusterItemClickListener(
            ClusterItemClickListener clusterItemClickListener) {
        mClusterItemClickListener = clusterItemClickListener;
    }

    /**
     * 添加一个聚合点
     *
     * @param item
     */
    public void addClusterItem(ClusterItem item) {
        Message message = Message.obtain();
        message.what = SignClusterHandler.CALCULATE_SINGLE_CLUSTER;
        message.obj = item;
        mSignClusterHandler.sendMessage(message);
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        mSignClusterHandler.removeCallbacksAndMessages(null);
        mMarkerhandler.removeCallbacksAndMessages(null);
        mSignClusterThread.quit();
        mMarkerHandlerThread.quit();
        for (Marker marker : mAddMarkers) {
            marker.remove();
        }
        mAddMarkers.clear();
        mClusters.clear();
        mClusterItems.clear();
        mLruCache.clear();
        mViewLruCache.clear();
    }

    //初始化Handler
    private void initThreadHandler() {
        mMarkerHandlerThread.start();
        mSignClusterThread.start();
        mMarkerhandler = new MarkerHandler(mMarkerHandlerThread.getLooper());
        mSignClusterHandler = new SignClusterHandler(mSignClusterThread.getLooper());
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {


    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        //缩放地图级别 刷新   判断是否是聚合 聚合才刷新
        if (mClusterSize == 0) return;

        if (zoom != 0 && Math.abs(zoom - arg0.zoom) > 0.5) {
            mPXInMeters = mAMap.getScalePerPixel();
            mClusterDistance = mPXInMeters * mClusterSize;
            assignClusters();
        }
        zoom = arg0.zoom;
    }

    //marker 点击事件
    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mClusterClickListener == null) {
            return true;
        }
        Cluster cluster = (Cluster) arg0.getObject();
        if (cluster == null) return false;
        if (cluster.getClusterCount() > 1) {//聚合点 点击事件
            mClusterClickListener.onClick(arg0, cluster.getClusterItems());
        } else {//单个点 点击事件
            mClusterItemClickListener.onClick(arg0, cluster.getClusterItems().get(0));
        }
        return true;
    }


    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        Log.e("addClusterToMap: ", clusters.size() + "");
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        removeMarkers.addAll(mAddMarkers);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        MyAnimationListener myAnimationListener = new MyAnimationListener(removeMarkers);
        for (Marker marker : removeMarkers) {
            marker.setAnimation(alphaAnimation);
            marker.setAnimationListener(myAnimationListener);
            marker.startAnimation();
        }

        for (Cluster cluster : clusters) {
            addSingleClusterToMap(cluster);
        }
    }

    private AlphaAnimation mADDAnimation = new AlphaAnimation(0, 1);

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param cluster
     */
    private void addSingleClusterToMap(Cluster cluster) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.anchor(0.1f, 0.5f).icon(getBitmapDes(cluster)).position(latlng);
        Marker marker = mAMap.addMarker(markerOptions);
        marker.setAnimation(mADDAnimation);
        marker.setObject(cluster);
        //修改  添加通过latlng查找marker
        if (point != null && point.toString().equals(latlng.toString())) {
            popupMarker = marker;
            marker.showInfoWindow();
        }
        marker.startAnimation();
        cluster.setMarker(marker);
        mAddMarkers.add(marker);

    }


    private void calculateClusters() {
        mIsCanceled = false;
        mClusters.clear();
//        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        for (ClusterItem clusterItem : mClusterItems) {
//            if (mIsCanceled) {
//                return;
//            }
            LatLng latlng = clusterItem.getPosition();
//            if (visibleBounds.contains(latlng)) {
            Cluster cluster = getCluster(latlng, mClusters);
            if (cluster != null) {
                cluster.addClusterItem(clusterItem);
            } else {
                cluster = new Cluster(latlng);
                mClusters.add(cluster);
                cluster.addClusterItem(clusterItem);
            }
//            }
        }

        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<Cluster>();
        clusters.addAll(mClusters);
        Message message = Message.obtain();
        message.what = MarkerHandler.ADD_CLUSTER_LIST;
        message.obj = clusters;
        if (mIsCanceled) {
            return;
        }
        mMarkerhandler.sendMessage(message);
    }

    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        mIsCanceled = true;
        mSignClusterHandler.removeMessages(SignClusterHandler.CALCULATE_CLUSTER);
        mSignClusterHandler.sendEmptyMessage(SignClusterHandler.CALCULATE_CLUSTER);
    }

    /**
     * 在已有的聚合基础上，对添加的单个元素进行聚合
     *
     * @param clusterItem
     */
    private void calculateSingleCluster(ClusterItem clusterItem) {
        LatLngBounds visibleBounds = mAMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng)) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.UPDATE_SINGLE_CLUSTER;

            message.obj = cluster;
            mMarkerhandler.removeMessages(MarkerHandler.UPDATE_SINGLE_CLUSTER);
            mMarkerhandler.sendMessageDelayed(message, 5);

        } else {
            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);
            Message message = Message.obtain();
            message.what = MarkerHandler.ADD_SINGLE_CLUSTER;
            message.obj = cluster;
            mMarkerhandler.sendMessage(message);
        }
    }

    /**
     * 根据一个点获取是否可以依附的聚合点，没有则返回null
     *
     * @param latLng
     * @return
     */
    private Cluster getCluster(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance && mAMap.getCameraPosition().zoom < 19) {
                return cluster;
            }
        }

        return null;
    }

//-------------------------------------------------------------------------

    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(Cluster cluster) {
        int num = cluster.getClusterCount();
        if (num <= 1) {
//            LocationBean bean = cluster.getClusterItems().get(0).getLocationBean();
            BitmapDescriptor bitmapDescriptor = mViewLruCache.get(cluster.getClusterItems().get(0).getTitle());
            if (bitmapDescriptor == null) {
                View view = View.inflate(mContext, R.layout.mark_layout, null);
                ImageView imageView = view.findViewById(R.id.img_mark_pic);
                TextView textView = view.findViewById(R.id.tv_mark_name);
                textView.setText(cluster.getClusterItems().get(0).getTitle());
//                try {
//                    textView.setText(bean.getTitle());
//                    imageView.setImageBitmap(Ion.with(mContext).load(bean.getImgUrl()).asBitmap().get());
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
                bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                mViewLruCache.put(cluster.getClusterItems().get(0).getTitle(), bitmapDescriptor);
            }
            return bitmapDescriptor;
        } else {
            BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
            if (bitmapDescriptor == null) {
                TextView textView = new TextView(mContext);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                textView.setText(String.valueOf(num));
                if (mClusterRender != null && mClusterRender.getDrawAble(num) != null) {
                    textView.setBackgroundDrawable(mClusterRender.getDrawAble(num));
                } else {
                    textView.setBackgroundResource(R.drawable.bg_blue_yuan);
                }
                bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
                mLruCache.put(num, bitmapDescriptor);
            }
            return bitmapDescriptor;
        }

//        BitmapDescriptor bitmapDescriptor = mLruCache.get(num);
//        if (bitmapDescriptor == null) {
//            if (num <= 1) {
//                LocationBean bean = cluster.getClusterItems().get(0).getLocationBean();
//                View view = View.inflate(mContext, R.layout.mark_layout, null);
//                ImageView imageView = view.findViewById(R.id.img_mark_pic);
//                TextView textView = view.findViewById(R.id.tv_mark_name);
//                try {
//                    textView.setText(bean.getTitle());
//                    imageView.setImageBitmap(Ion.with(mContext).load(bean.getImgUrl()).asBitmap().get());
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//                return BitmapDescriptorFactory.fromView(view);
//            } else {
//                TextView textView = new TextView(mContext);
//                textView.setGravity(Gravity.CENTER);
//                textView.setTextColor(Color.WHITE);
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//                textView.setText(String.valueOf(num));
//                if (mClusterRender != null && mClusterRender.getDrawAble(num) != null) {
//                    textView.setBackgroundDrawable(mClusterRender.getDrawAble(num));
//                } else {
//                    textView.setBackgroundResource(R.mipmap.marker_bg);
//                }
//                bitmapDescriptor = BitmapDescriptorFactory.fromView(textView);
//            }
//
//            mLruCache.put(num, bitmapDescriptor);
//        }
//        return bitmapDescriptor;
    }


    /**
     * 添加新点
     *
     * @param clusterItems
     */
    public void setMorePoint(List<ClusterItem> clusterItems) {
        Log.e("mClusterItems=  ",clusterItems.size()+"");
        mClusterItems = clusterItems;
        mViewLruCache.clear();//清除缓存数据
        assignClusters();
    }

//---------------------------------------------------------------------------------------------------------------------

    /**
     * 更新已加入地图聚合点的样式
     */
    private void updateCluster(Cluster cluster) {
        Marker marker = cluster.getMarker();
        marker.setIcon(getBitmapDes(cluster));
    }

    /**
     * 聚合与展开
     */
    public void updateClusters(boolean isCluster) {
        if (isCluster) {
            mClusterSize = 0;
        } else {
            mClusterSize = clusterSize;
        }
        mClusterDistance = mPXInMeters * mClusterSize;
        assignClusters();
    }


//-----------------------辅助内部类用---------------------------------------------

    /**
     * marker渐变动画，动画结束后将Marker删除
     */
    class MyAnimationListener implements Animation.AnimationListener {
        private List<Marker> mRemoveMarkers;

        MyAnimationListener(List<Marker> removeMarkers) {
            mRemoveMarkers = removeMarkers;
        }

        @Override
        public void onAnimationStart() {

        }

        @Override
        public void onAnimationEnd() {
            for (Marker marker : mRemoveMarkers) {
                marker.remove();
            }
            mRemoveMarkers.clear();
        }
    }

    /**
     * 处理market添加，更新等操作
     */
    class MarkerHandler extends Handler {

        static final int ADD_CLUSTER_LIST = 0;

        static final int ADD_SINGLE_CLUSTER = 1;

        static final int UPDATE_SINGLE_CLUSTER = 2;

        MarkerHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {

            switch (message.what) {
                case ADD_CLUSTER_LIST:
                    List<Cluster> clusters = (List<Cluster>) message.obj;
                    addClusterToMap(clusters);
                    break;
                case ADD_SINGLE_CLUSTER:
                    Cluster cluster = (Cluster) message.obj;
                    addSingleClusterToMap(cluster);
                    break;
                case UPDATE_SINGLE_CLUSTER:
                    Cluster updateCluster = (Cluster) message.obj;
                    updateCluster(updateCluster);
                    break;
            }
        }
    }

    /**
     * 处理聚合点算法线程
     */
    class SignClusterHandler extends Handler {
        static final int CALCULATE_CLUSTER = 0;
        static final int CALCULATE_SINGLE_CLUSTER = 1;

        SignClusterHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case CALCULATE_CLUSTER:
                    calculateClusters();
                    break;
                case CALCULATE_SINGLE_CLUSTER:
                    ClusterItem item = (ClusterItem) message.obj;
                    mClusterItems.add(item);
                    calculateSingleCluster(item);
                    break;
            }
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * 显示popupWindow
     *
     * @param latLng
     */
    public void showPopup(LatLng latLng) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        LatLngBounds latLngBounds = builder.build();
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
        this.point = latLng;
        assignClusters();
    }

    /**
     * 隐藏popupWindow
     */
    public void hiedPopup() {
        point = null;
        if (popupMarker != null) {
            popupMarker.hideInfoWindow();
        }
    }

    /**
     * 移除单个marker
     */
    public void removeMarker() {
        if (popupMarker != null) {
            popupMarker.remove();
            popupMarker = null;
        }
    }
}