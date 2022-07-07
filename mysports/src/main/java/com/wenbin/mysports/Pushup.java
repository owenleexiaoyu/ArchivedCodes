package com.wenbin.mysports;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wenbin.mysports.data.DataUtils;
import com.wenbin.mysports.data.MainActivity;

import java.text.DecimalFormat;

public class Pushup extends AppCompatActivity implements SensorEventListener {

    private Button butStart = null;
    private Button butSubmit = null;
    DecimalFormat nf = new DecimalFormat("0");
    private SensorManager mSensorMgr = null;
    Sensor mSensor = null;
    private int mGX = 0;
    private TextView fwcGs = null;
    private boolean in = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pushup);
        //取得组件
        fwcGs = (TextView) findViewById(R.id.textViewGs);
        butStart = (Button) super.findViewById(R.id.butStart);
        butSubmit = (Button) super.findViewById(R.id.butSubmit);
        //SensorMannager 传感器管理对象
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取近距离感应器
        mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 注册 listener,第三个参数是检测的精确度
                mSensorMgr.registerListener(Pushup.this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
                butStart.setVisibility(View.GONE);
                butSubmit.setVisibility(View.VISIBLE);
            }
        });
        butSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtils.getInstance(getApplicationContext()).savePushup(fwcGs.getText().toString());
                finish();

            }
        });
    }
    //获取近距离感应器状态变化
    public void onSensorChanged(SensorEvent event) {
        //身体接近近距离感应器计半个数,离开感应器计半个数
        if(in) {
            mGX ++;
            in = false;
            fwcGs.setText(""+mGX);
        }else {
            in = true;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        mSensorMgr.unregisterListener(this,mSensor);

        super.onStop();
    }
}



