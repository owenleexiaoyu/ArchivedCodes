package com.redant.childcare.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cn.bmob.push.PushConstants;

/**
 * Created by 啸宇 on 2017-10-29.
 */

public class MyPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyPushReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.i("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));
            Toast.makeText(context, "儿童端收到推送内容:"+intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
        }
    }
}
