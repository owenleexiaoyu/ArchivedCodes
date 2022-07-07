package com.wenbin.mysports.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.wenbin.SettingActivity;
import com.wenbin.view.Constant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wenbin on 2016/2/20.
 */
public class DataUtils {
    private Context context = null;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private List<Date[]> listDate = null;
    private List<double[]> listDouble = null;
    private static DataUtils utils = null;
    private SharedPreferences sp = null;
    private static final String TAG = "DataUtils";
    //定义一组常量，用于标识五种运动的次序
    public static final int UTILS_SPORT_RUN = 0;
    public static final int UTILS_SPORT_SQUAT = 1;
    public static final int UTILS_SPORT_PUSHUP = 2;
    public static final int UTILS_SPORT_WALK = 3;
    public static final int UTILS_SPORT_BASKETBALL = 4;

    private DataUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("mypreference",Context.MODE_PRIVATE);
        sp = context.getSharedPreferences(SettingActivity.CALORIE_GOAL,Context.MODE_PRIVATE);
        editor = preferences.edit();
        listDate = new ArrayList<>();
        listDouble = new ArrayList<>();
    }
    public static DataUtils getInstance(Context context) {
        if(utils == null) {
            utils = new DataUtils(context);
        }
        return utils;
    }

    /**
     * 此方法用来存储跑步距离
     * @param distance
     */
    public void saveRun(String distance) {

        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        String res = distance+"@@0@@0@@0@@0";
        if(dis == null) {
            editor.putString(dateKey,res);
            Constant.run = Integer.parseInt(distance);
        }else {
            String[] tmp = dis.split("@@");
            int myDis = Integer.parseInt(tmp[UTILS_SPORT_RUN]);
            myDis += Integer.parseInt(distance);
            Constant.run = myDis;
            res = myDis+"@@"+tmp[UTILS_SPORT_SQUAT]+"@@"+tmp[UTILS_SPORT_PUSHUP]+"@@"+tmp[UTILS_SPORT_WALK]+"@@"+tmp[UTILS_SPORT_BASKETBALL];
            editor.putString(dateKey,res);
        }
        editor.commit();

    }

    /**
     * 此方法用来存储深蹲量
     * @param distance
     */
    public void saveSquat(String distance) {
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        String res = "0@@"+distance+"@@0@@0@@0";
        if(dis == null) {
            editor.putString(dateKey,res);
            Constant.squat = Integer.parseInt(distance);
        }else {
            String[] tmp = dis.split("@@");
            int myDis = Integer.parseInt(tmp[UTILS_SPORT_SQUAT]);
            myDis += Integer.parseInt(distance);
            Constant.squat = myDis;
            res = tmp[UTILS_SPORT_RUN]+"@@"+myDis+"@@"+tmp[UTILS_SPORT_PUSHUP]+"@@"+tmp[UTILS_SPORT_WALK]+"@@"+tmp[UTILS_SPORT_BASKETBALL];
            editor.putString(dateKey,res);
        }
        editor.commit();
    }

    /**
     * 此方法用来存储俯卧撑量
     * @param distance
     */
    public void savePushup(String distance) {
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        String res = "0@@0@@"+distance+"@@0@@0";
        if(dis == null) {
            editor.putString(dateKey,res);
            Constant.pushup = Integer.parseInt(distance);
        }else {
            String[] tmp = dis.split("@@");
            int myDis = Integer.parseInt(tmp[UTILS_SPORT_PUSHUP]);
            myDis += Integer.parseInt(distance);
            Constant.pushup = myDis;
            res = tmp[UTILS_SPORT_RUN]+"@@"+tmp[UTILS_SPORT_SQUAT]+"@@"+myDis+"@@"+tmp[UTILS_SPORT_WALK]+"@@"+tmp[UTILS_SPORT_BASKETBALL];
            editor.putString(dateKey,res);
        }
        editor.commit();
    }

    /**
     * 用来存储记步的
     * @param distance
     */
    public void saveWalk(String distance) {
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        String res = "0@@0@@0@@"+distance+"@@0";
        if(dis == null) {
            editor.putString(dateKey,res);
            Constant.walk = Integer.parseInt(distance);
        }else {
            String[] tmp = dis.split("@@");
            int myDis = Integer.parseInt(tmp[UTILS_SPORT_WALK]);
            myDis += Integer.parseInt(distance);
            Constant.walk = myDis;
            res = tmp[UTILS_SPORT_RUN]+"@@"+tmp[UTILS_SPORT_SQUAT]+"@@"+tmp[UTILS_SPORT_PUSHUP]+"@@"+myDis+"@@"+tmp[UTILS_SPORT_BASKETBALL];
            editor.putString(dateKey,res);
        }
        editor.commit();
    }

    /**
     * 用来存储篮球训练的
     * @param distance
     */

    public void saveBasketball(String distance) {
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        String res = "0@@0@@0@@0@@"+distance;
        if(dis == null) {
            editor.putString(dateKey,res);
            Constant.basketball = Integer.parseInt(distance);
        }else {
            String[] tmp = dis.split("@@");
            int myDis = Integer.parseInt(tmp[UTILS_SPORT_BASKETBALL]);
            myDis += Integer.parseInt(distance);
            Constant.basketball = myDis;
            res = tmp[UTILS_SPORT_RUN]+"@@"+tmp[UTILS_SPORT_SQUAT]+"@@"+tmp[UTILS_SPORT_PUSHUP]+"@@"+tmp[UTILS_SPORT_WALK]+"@@"+myDis;
            editor.putString(dateKey,res);
        }
        editor.commit();
    }
    public void updateNewDayData(){
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        if(dis == null){
            //新的一天，把所有的数据归零
            editor.putString(dateKey,"0@@0@@0@@0@@0");
            editor.commit();
        }
    }
    public int getData(int item){
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        if(dis == null){
            return 0;
        }else{
            String[] tmp = dis.split("@@");
            return Integer.parseInt(tmp[item]);
        }
    }
    public int getDateCount(){
        Map<String,?> map = preferences.getAll();
        return map.size();
    }
    public double get(int item) {
        getListDate();
        String dateKey = getDateKey();
        String dis = preferences.getString(dateKey,null);
        if(dis == null){
            return 0.0;
        }else{
            String[] tmp = dis.split("@@");
            return getCalorie(item,Integer.parseInt(tmp[item]));
        }
    }
    public List<Date[]> getListDate() {
        Map<String,?> map = preferences.getAll();
        Log.d(TAG, "getListDate: "+map.size());
        Date[] date = new Date[map.size()];
        double[] valuesRun = new double[map.size()];
        double[] valueSquat = new double[map.size()];
        double[] valuePushup = new double[map.size()];
        double[] valueWalk = new double[map.size()];
        double[] valueBasketball = new double[map.size()];
        int i = 0;
        for (String key : map.keySet()) {
            Log.d("sop",key);
            date[i] = getDate(key);
            String tm = map.get(key).toString();
            valuesRun[i] = getCalorie(UTILS_SPORT_RUN,Double.parseDouble(map.get(key).toString().split("@@")[UTILS_SPORT_RUN]));
            valueSquat[i] = getCalorie(UTILS_SPORT_SQUAT,Double.parseDouble(map.get(key).toString().split("@@")[UTILS_SPORT_SQUAT]));
            valuePushup[i] = getCalorie(UTILS_SPORT_PUSHUP,Double.parseDouble(map.get(key).toString().split("@@")[UTILS_SPORT_PUSHUP]));
            valueWalk[i] = getCalorie(UTILS_SPORT_WALK,Double.parseDouble(map.get(key).toString().split("@@")[UTILS_SPORT_WALK]));
            valueBasketball[i] = getCalorie(UTILS_SPORT_BASKETBALL,Double.parseDouble(map.get(key).toString().split("@@")[UTILS_SPORT_BASKETBALL]));
            Log.d("sop","---"+tm);
            i++;
        }
        listDate.clear();
        listDouble.clear();
        for(i = 0 ;i < 5;i++) {
            listDate.add(date);
        }
        listDouble.add(valuesRun);
        listDouble.add(valueSquat);
        listDouble.add(valuePushup);
        listDouble.add(valueWalk);
        listDouble.add(valueBasketball);
        return listDate;
    }
    public List<double[]> getYValues() {
        return listDouble;
    }
    private String format(int data) {
        if(data < 10) {
            return "0"+data;
        }else {
            return "" + data;
        }
    }
    /**
     * 根据当前日期获取统一格式的key值，通过该key值可以查询到当天的五种运动的数据
     */
    public String getDateKey() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String key = year+format(month)+format(day);
        return key;
    }
    private Date getDate(String s) {
        int year = Integer.parseInt(s.substring(0,4));
        int month = Integer.parseInt(s.substring(4,6));
        int day = Integer.parseInt(s.substring(6,8));
        return new Date(year,month-1,day);
    }
    public double getCalorie(int item,double data) {
        double res = 0;
        switch (item) {
            case UTILS_SPORT_RUN://跑步
                res = Float.valueOf(sp.getString(SettingActivity.KEY_INFO_WEIGHT,"50"))*data*1.036/1000;
                break;
            case UTILS_SPORT_SQUAT://深蹲
                res = data * 1.2;
                break;
            case UTILS_SPORT_PUSHUP://俯卧撑
                res = data * 2;
                break;
            case UTILS_SPORT_WALK://记步
                res = Float.valueOf(sp.getString(SettingActivity.KEY_INFO_WEIGHT,"50"))*data*0.82/1000;
                break;
            case UTILS_SPORT_BASKETBALL://篮球训练
                res = Float.valueOf(sp.getString(SettingActivity.KEY_INFO_WEIGHT,"50"))*data*1.036/1000;
                break;
        }
        DecimalFormat df = new DecimalFormat(".00");
        double result = Double.parseDouble(df.format(res));
        return result;
    }
}
