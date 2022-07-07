package com.redant.childcare.services;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

/**
 * Created by Administrator on 2017-06-01.
 */

public class FallCountDetector implements SensorEventListener {
    public static int FALLCOUNTS;//跌倒次数
    int jumpCount = 0;
    long startTime = 0;
    long endTime = 0;
    int totalCount = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //下面这一段是用来识别跳跃的
        // 传感器信息改变时执行该方法
        float[] values = sensorEvent.values;
        float x = values[0]; // x轴方向的重力加速度，向右为正
        float y = values[1]; // y轴方向的重力加速度，向前为正
        float z = values[2]; // z轴方向的重力加速度，向上为正
        // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
        int medumValue = 40;
        if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
            totalCount++;
            if (jumpCount == -1) {
                startTime = endTime = System.currentTimeMillis();
            } else {
                endTime = System.currentTimeMillis();
            }
            if (endTime - startTime < 1000) {
                jumpCount++;
            }
            startTime = endTime;
            FALLCOUNTS = totalCount - jumpCount;
            Log.i("FallService","FALLCOUNTS----------------->"+ FALLCOUNTS);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
