package com.redant.childcare.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.NotificationCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.redant.childcare.MainActivity;
import com.redant.childcare.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/9.
 */

public class UtilTools {
    //为textview设置自定义字体
    public static void setFontToText(Context mContext, TextView textView){
        Typeface fonttf = Typeface.createFromAsset(mContext.getAssets(), "number.ttf");
        textView.setTypeface(fonttf);
    }
    //输出Toast信息
    public static void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    //获取保存数据的日期key格式
    public static String getdateFormatKey(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return format.format(date);
    }
    //下载当天的运动数据
    public static String downloadData(String cusername){
        HttpParams params = new HttpParams();
        params.put("action",AppConfig.ACTION_DOWNLOAD);
        params.put("username",cusername);
        RxVolley.post(AppConfig.SERVICE_URL, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Logging.i("onSuccess",t);
                //处理接受到的数据
            }
        });
        return null;
    }

    public static void showNotification(Context context, String title, String content, Intent intent) {
        PendingIntent pi = PendingIntent.getActivity(context, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.childcare_circle)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .build();
        manager.notify(1,notification);
    }
}
