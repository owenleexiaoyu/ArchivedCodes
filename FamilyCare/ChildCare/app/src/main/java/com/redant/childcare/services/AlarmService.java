package com.redant.childcare.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.redant.childcare.model.MyUser;
import com.redant.childcare.model.SportData;
import com.redant.childcare.receivers.AlarmReceiver;
import com.redant.childcare.ui.SettingActivity;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 啸宇 on 2017-09-18.
 */

public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //上传运动数据
        int steps = ShareUtils.getInt(this, UtilTools.getdateFormatKey1(),0);
        int falls = ShareUtils.getInt(this, UtilTools.getdateFormatKey2(),0);
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null){
            try {
                update(steps,falls);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "未登录，请登录后上传", Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void update(int steps, int falls) throws JSONException {
//        SportData data = new SportData();
//        data.setStep(steps);
//        data.setFall(falls);
//        data.setCId(BmobUser.getCurrentUser().getObjectId());
//        data.setDate(new BmobDate(new Date()));
//        data.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if(e == null){
//                    //上传成功
//                    Toast.makeText(getApplicationContext(), "上传成功:"+s,
//                            Toast.LENGTH_SHORT).show();
//                }else{
//                    //上传失败
//                    Toast.makeText(getApplicationContext(), "上传失败:"+s,
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        String data = "{\n" +
                "    \"code\": 0,\n" +
                "    \"msg\": {\n" +
                "        \"child\": "+BmobUser.getCurrentUser(MyUser.class).getNickName()+",\n" +
                "        \"step\": "+steps+",\n" +
                "        \"fall\": "+falls+"\n" +
                "    }\n" +
                "}";
        JSONObject object = new JSONObject(data);
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        List<String> channels = new ArrayList<>();
        //TODO 替换成你需要推送的所有频道，推送前请确认已有设备订阅了该频道，也就是channels属性存在该值
        channels.add(BmobUser.getCurrentUser().getObjectId());
        query.addWhereContainedIn("channels", channels);
        bmobPushManager.setQuery(query);
        bmobPushManager.pushMessage(object, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    Log.i("TAG","推送成功！");
                    Toast.makeText(AlarmService.this, "发送数据成功", Toast.LENGTH_SHORT).show();
                }else {
                    Log.e("TAG","异常：" + e.getMessage());
                    Toast.makeText(AlarmService.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
