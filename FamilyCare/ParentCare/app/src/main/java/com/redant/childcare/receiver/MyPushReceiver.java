package com.redant.childcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.redant.childcare.MainActivity;
import com.redant.childcare.entry.DaliyData;
import com.redant.childcare.ui.ItemContentActivity;
import com.redant.childcare.utils.UtilTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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
//            Toast.makeText(context, "客户端收到推送内容:"+intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
            try {
                JSONObject object = new JSONObject(intent.getStringExtra("msg"));
                int code = object.getInt("code");
                switch (code){
                    case 0:
                        //收到了运动数据
                        JSONObject data = object.getJSONObject("msg");
                        String childName = data.getString("child");
                        int step = data.getInt("step");
                        int fall = data.getInt("fall");
                        handleSportData(context, childName, step, fall);
//                        Toast.makeText(context, childName+" 今天走了"+step+"步，跌倒了"+fall+"次", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //收到了示警信号
                        JSONObject data1 = object.getJSONObject("msg");
                        String childName1 = data1.getString("child");
                        showWornning(context, childName1);
//                        Toast.makeText(context, childName1+" 离开了安全范围，请尽快联系，以防走失", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showWornning(Context context, String name) {
        Intent intent = new Intent(context, MainActivity.class);
        UtilTools.showNotification(context, "注意", name+"离开了安全范围，请尽快联系，以防走失",intent);
    }

    private void handleSportData(Context context, String name, int step, int fall) {
        //保存到本地的数据库中
        DaliyData data = new DaliyData();
        data.setChildName(name);
        data.setStep(step);
        data.setFall(fall);
        data.setDate(new Date());
        data.save();
        Intent intent = new Intent(context, ItemContentActivity.class);
        intent.putExtra(ItemContentActivity.EXTRA_KEY_STEP, step);
        intent.putExtra(ItemContentActivity.EXTRA_KEY_FALL, fall);
        intent.putExtra(ItemContentActivity.EXTRA_KEY_NAME, name);
        UtilTools.showNotification(context, "运动", name+"今天的运动情况已更新，请点击查看",intent);
    }
}
