package com.redant.childcare.ui;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.redant.childcare.R;
import com.redant.childcare.model.MyUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

/**
 * Created by lenovo on 2017/6/26.
 */

public class MapActivity extends BaseActivity implements LocationSource,
        AMapLocationListener {
    private int flag = 0;
    private double bLatitude = 0;//纬度
    private double bLongitude = 0;//经度
    private double nLatitude = 0;//纬度
    private double nLongitude = 0;//经度
    private long bTime = 0;
    private long nTime = 0;
    private double speed = 0;
    private String time = "00:00:00";
    private double distance = 0;


    private Vibrator vibrator;
    private LatLng centerLatLng;
    final int REQ_LOCATION = 0x12;
    final int REQ_GEO_FENCE = 0x13;
    final String ACTION_GEO_FENCE = "geo fence action";
    private IntentFilter intentFilter;

    private MapView mapView;
    private TextView tvTime = null;
    private TextView tvSpeed = null;
    private TextView tvDistance = null;

    private OnLocationChangedListener onLocationChangedListener;
    private AMapLocationClient mlocationClient;
    private AMap aMap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    DecimalFormat df = new DecimalFormat("#.00");

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvDistance.setText(distance + "");
            tvSpeed.setText(speed + "m/s");
            nTime = System.currentTimeMillis();
            long tmp = nTime - bTime;
            int hour = (int) (tmp / (1000 * 60 * 60));
            tmp -= hour * 1000 * 60 * 60;
            int minute = (int) (tmp / (1000 * 60));
            tmp -= minute * 1000 * 60;
            int second = (int) (tmp / (1000));
            time = format(hour) + ":" + format(minute) + ":" + format(second);
            tvTime.setText(time);
            super.handleMessage(msg);
        }

        private String format(int data) {
            if (data < 10) {
                return "0" + data;
            } else {
                return data + "";
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        bTime = nTime = System.currentTimeMillis();
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        //在onCreat方法中给aMap对象赋值
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式

        //1.获取震动服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //处理进出地理围栏事件
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GEO_FENCE);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            applyPermission();
        }
        setUpMap();
        setLocation();
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.arrow));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));// 设置圆形的填充颜色
        myLocationStyle.anchor(0.5f, 0.5f);//设置小蓝点的锚点
        //myLocationStyle.strokeWidth(0.5f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setCompassEnabled(true);
        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(50));
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //aMap.setMyLocationType(Amap.LOCATION_TYPE_M);
    }

    private void setLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver
            broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(ACTION_GEO_FENCE)) {
                Bundle bundle = intent.getExtras();
                // 根据广播的event来确定是在区域内还是在区域外
                int status = bundle.getInt("event");
                String geoFenceId = bundle.getString("fenceId");
                if (status == 1) {
                    Toast.makeText(MapActivity.this, "进入地理围栏~", Toast.LENGTH_LONG).show();
                    vibrator.vibrate(3000);
                } else if (status == 2) {
                    // 离开围栏区域
                    Toast.makeText(MapActivity.this, "离开地理围栏~", Toast.LENGTH_LONG).show();
                    vibrator.vibrate(3000);
                    //发送示警信号给家长
                    try {
                        sendWarning();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    /**
     * 方法必须重写
     */

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void applyPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationClient.startLocation();
            } else {
                Toast.makeText(MapActivity.this, "没有权限，无法获取位置信息~", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            if (flag < 5) {
                nLatitude = amapLocation.getLatitude();
                nLongitude = amapLocation.getLongitude();
                flag++;
            } else {
                bLatitude = nLatitude;
                bLongitude = nLongitude;
                nLatitude = amapLocation.getLatitude();
                nLongitude = amapLocation.getLongitude();
                //计算并更新数据
                speed = amapLocation.getSpeed();
                LatLng startLatlng = new LatLng(bLatitude, bLongitude);
                LatLng endLatlng = new LatLng(nLatitude, nLongitude);
                double tmp = AMapUtils.calculateLineDistance(startLatlng, endLatlng);
                tmp = Double.parseDouble(df.format(tmp));
                if (tmp > 0.4 && tmp < 10) {
                    distance += tmp;
                }
                if (tmp < 10) {
                    aMap.addPolyline((new PolylineOptions()).add(
                            new LatLng(bLatitude, bLongitude), new LatLng(nLatitude, nLongitude)).color(
                            Color.BLUE).width(1));
                    aMap.invalidate();
                }
                handler.sendEmptyMessage(0);
            }
            //设置地理围栏
            if (centerLatLng == null) {
                centerLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                Intent intent = new Intent(ACTION_GEO_FENCE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //100:是围栏半径（测试发现，设置的太小，不会发出广播）；-1：是超时时间（单位：ms，-1代表永不超时）
                mLocationClient.addGeoFenceAlert("fenceId", centerLatLng.latitude, centerLatLng.longitude, 100, -1, pendingIntent);
                addCircle(centerLatLng, 100);
            } else {
                double latitude = amapLocation.getLatitude();
                double longitude = amapLocation.getLongitude();
                //Log.d(tag, "当前经纬度: " + latitude + "," + longitude);
                LatLng endLatlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                // 计算量坐标点距离
                double distances = AMapUtils.calculateLineDistance(centerLatLng, endLatlng);
//                Toast.makeText(MapActivity.this, "当前距离中心点：" + ((int) distances), Toast.LENGTH_LONG).show();
                if (onLocationChangedListener != null) {
                    onLocationChangedListener.onLocationChanged(amapLocation);
                }
            }
        }
    }

    public void addCircle(LatLng latLng, int radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(4);
        circleOptions.strokeColor(Color.RED);
        // circleOptions.fillColor(Color.BLUE);
        aMap.addCircle(circleOptions);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //DataUtils datautils = DataUtils.getInstance(getApplicationContext());
        //datautils.saveRun(tvDistance.getText().toString());
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
        super.onDestroy();
        mapView.onDestroy();
    }
    
    @Override
    public void activate(OnLocationChangedListener listener) {
        onLocationChangedListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        onLocationChangedListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    private void sendWarning() throws JSONException {
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        List<String> channels = new ArrayList<>();
        //TODO 替换成你需要推送的所有频道，推送前请确认已有设备订阅了该频道，也就是channels属性存在该值
        channels.add(BmobUser.getCurrentUser().getObjectId());
        query.addWhereContainedIn("channels", channels);
        bmobPushManager.setQuery(query);
        String data = "{\n" +
                "    \"code\": 1,\n" +
                "    \"msg\": {\n" +
                "        \"child\": "+BmobUser.getCurrentUser(MyUser.class).getNickName()+"\n" +
                "    }\n" +
                "}";
        JSONObject object = new JSONObject(data);
        bmobPushManager.pushMessage(object, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("TAG", "推送成功！");
                    Toast.makeText(MapActivity.this, "发送示警信号成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TAG", "异常：" + e.getMessage());
                    Toast.makeText(MapActivity.this, "发送示警信号失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

