/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.wenbin.mysports;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.wenbin.mysports.data.DataUtils;

import java.text.DecimalFormat;


public class Basketball extends AppCompatActivity {
    private SensorManager sensorManager;
    private TextView jumpValue = null;
    private TextView accTimes = null;
    private TextView runCountValue = null;
    private TextView runDistanceValue = null;
    private TextView runCalorieValue = null;
    private Chronometer sportsTime = null;
    private Button mySb = null;
    DecimalFormat df = new DecimalFormat("0.00");

    private static final String TAG = "TestSensorActivity";
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
        setContentView(R.layout.activity_basketball);
        jumpValue = (TextView) findViewById(R.id.jumpValue);
        accTimes = (TextView) findViewById(R.id.accTimes);
        runCountValue = (TextView) findViewById(R.id.runCountValue);
        runDistanceValue = (TextView) findViewById(R.id.runDistanceValue);
        runCalorieValue = (TextView) findViewById(R.id.runCalorieValue);
        mySb = (Button) findViewById(R.id.mySb);
        sportsTime = (Chronometer) findViewById(R.id.sportsTime);
        sportsTime.setBase(SystemClock.elapsedRealtime());
        //tv.setText("");
        mySb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {// ???????????????
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // ??????????????????Listener?????????????????????????????????????????????????????????????????????????????????????????????
        }

        sportsTime.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {// ???????????????
            sensorManager.unregisterListener(sensorEventListener);
        }
        long time = (SystemClock.elapsedRealtime() - sportsTime.getBase())/1000;
        DataUtils.getInstance(getApplicationContext()).saveBasketball(time + "");
        sportsTime.stop();
    }

    /**
     * ??????????????????
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            //???????????????????????????????????????
///////////////////////////////////////////////////////////////////////////////

            Message msg = new Message();
            // ???????????????????????????????????????
            float[] values = event.values;
            float x = values[0]; // x??????????????????????????????????????????
            float y = values[1]; // y??????????????????????????????????????????
            float z = values[2]; // z??????????????????????????????????????????
            //	Log.i(TAG, "x???????????????????????????" + x +  "???y???????????????????????????" + y +  "???z???????????????????????????" + z);
            // ????????????????????????????????????????????????40????????????????????????????????????
            int medumValue = 15;// ?????? i9250????????????????????????20????????????????????????19???
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                Log.i("sop", "x?????????" + x + "???y?????????" + y + "???z?????????" + z);
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
                //???????????????????????????????????????
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
            // TODO ???????????????????????????

        }
    };

    /**
     * ????????????
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            jumpValue.setText(jumpCount + "");
            accTimes.setText(pushCount + "");
            runCountValue.setText(runCount + "");
            runDistanceValue.setText(df.format(runCount * 1.62) + "");
            runCalorieValue.setText(df.format(DataUtils.getInstance(getApplicationContext()).getCalorie(4,runCount)));
        }
    };
}