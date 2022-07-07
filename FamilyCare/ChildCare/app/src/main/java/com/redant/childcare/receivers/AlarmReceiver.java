package com.redant.childcare.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.redant.childcare.services.AlarmService;
import com.redant.childcare.ui.AboutActivity;
import com.redant.childcare.utils.AlarmManagerUtil;

/**
 * Created by 啸宇 on 2017-09-18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: --->闹铃响了");
        // TODO Auto-generated method stub
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }
        Intent clockIntent = new Intent(context, AlarmService.class);
        context.startService(clockIntent);
    }
}
