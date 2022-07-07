package com.redant.childcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/4/9.
 * SharedPreferences封装类
 */

public class ShareUtils {
    //SharedPreferences对象名字
    private static final String NAME = "childcare";
    private static SharedPreferences sp;
    //存放String类型数据
    public static void putString(Context mContext, String key, String value){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    //读取String类型数据
    public static String getString(Context mContext, String key, String defValue){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }
    //存放int类型数据
    public static void putInt(Context mContext, String key, int value){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    //读取int类型数据
    public static int getInt(Context mContext, String key, int defValue){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getInt(key,defValue);
    }
    //存放boolean类型数据
    public static void putBoolean(Context mContext, String key, boolean value){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    //读取boolean类型数据
    public static boolean getBoolean(Context mContext, String key, boolean defValue){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }
    //删除某个键值对
    public static void delete(Context mContext, String key){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
    //删除所有数据
    public static void deleteAll(Context mContext){
        sp = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }
}
