package com.redant.childcare.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.redant.childcare.utils.Logging;

/**
 * Created by Administrator on 2017-06-01.
 * 次类用于在后台记录跌倒次数
 */

public class FallService extends Service {
    private boolean FLAG = false;//服务运行标志
    private SensorManager sensorManager;//传感器管理器
    private FallCountDetector detector;//传感器的事件监听器
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logging.d("FallService","onCreate");
        FLAG = true;//运行标记设为true
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        detector = new FallCountDetector();
        // 注册监听器
        // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        sensorManager.registerListener(detector,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FLAG = false;//运行标记设为false
        //监听器
        if (detector != null){
            sensorManager.unregisterListener(detector);
        }
        Log.i("Service","onDestory");
    }
}
