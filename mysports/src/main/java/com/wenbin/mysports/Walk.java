package com.wenbin.mysports;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wenbin.mysports.data.DataUtils;

import java.text.DecimalFormat;

public class Walk extends AppCompatActivity {
    private SensorManager sensorManager;
    private TextView walkValue = null;
    private TextView walkDistanceValue = null;
    private TextView speedValue = null;
    private TextView calorieValue = null;
    private Button btnSubmit;
    DecimalFormat df = new DecimalFormat("0.00");

    private static final int SENSOR_SHAKE = 10;
    int jumpCount = 0;
    long startTime = 0;
    long endTime = 0;
    int totalCount = 0;
    int pushCount = 0;
    int runCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        walkValue = (TextView) findViewById(R.id.step_value);
        speedValue = (TextView) findViewById(R.id.pace_value);
        walkDistanceValue = (TextView) findViewById(R.id.distance_value);
        calorieValue = (TextView) findViewById(R.id.calories_value);
        btnSubmit = (Button) findViewById(R.id.btMySubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DataUtils.getInstance(getApplicationContext()).saveWalk(runCount+"");
                finish();
            }
        });
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }

    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            //下面这一段是用来识别跳跃的
///////////////////////////////////////////////////////////////////////////////

            Message msg = new Message();
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            //	Log.i(TAG, "x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
            int medumValue = 10;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                Log.i("sop", "x轴方向" + x + "；y轴方向" + y + "；z轴方向" + z);
                totalCount++;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                msg.what = SENSOR_SHAKE;
                msg.obj = "x=" + x + "\ny=" + y + "\nz=" + z;
                if (jumpCount == -1) {
                    startTime = endTime = System.currentTimeMillis();
                } else {
                    endTime = System.currentTimeMillis();
                }
                if (endTime - startTime < 1000) {
                    jumpCount++;
                }
                startTime = endTime;

//////////////////////////////////////////////////////////////////////////////////////////////
                //如果不是跳跃，那么就是冲刺
                pushCount = totalCount - jumpCount;
            }
            medumValue = 11;
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                runCount++;
            }
            handler.sendMessage(msg);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO 自动生成的方法存根

        }
    };

    /**
     * 动作执行
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            speedValue.setText(jumpCount + "");
            walkValue.setText(runCount + "");
            walkDistanceValue.setText(df.format(runCount * 0.82) + "");
            calorieValue.setText(df.format(DataUtils.getInstance(getApplicationContext()).getCalorie(3,runCount)));
        }
    };
}