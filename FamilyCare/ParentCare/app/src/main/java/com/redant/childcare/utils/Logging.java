package com.redant.childcare.utils;

import android.util.Log;

/**
 * 打印日志的工具类
 * Created by Administrator on 2017/3/26.
 */

public class Logging {
    private static final boolean isDebug = true;
    public static void d(String Tag, String log){
        if(isDebug){
            Log.d(Tag, log);
        }
    }
    public static void i(String Tag, String log){
        if(isDebug){
            Log.d(Tag, log);
        }
    }
    public static void v(String Tag, String log){
        if(isDebug){
            Log.v(Tag, log);
        }
    }
    public static void e(String Tag, String log){
        if(isDebug){
            Log.e(Tag, log);
        }
    }
    public static void w(String Tag, String log){
        if(isDebug){
            Log.w(Tag, log);
        }
    }
}
