package com.redant.childcare.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

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
    public static void toast(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    //获取保存每日步行数据的日期key格式
    public static String getdateFormatKey1(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return format.format(date);
    }
    //获取保存每日跌倒数据的日期key格式
    public static String getdateFormatKey2(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return format.format(date);
    }
}
