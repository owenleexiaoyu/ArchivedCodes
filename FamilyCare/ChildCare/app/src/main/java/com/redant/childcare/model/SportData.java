package com.redant.childcare.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 每天运动的实体类，用于在服务器端保存数据
 *
 * Created by 啸宇 on 2017-10-26.
 */

public class SportData extends BmobObject{
    private int step;
    private int fall;
    private String cId;//此运动属于的儿童id
    private BmobDate date;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getFall() {
        return fall;
    }

    public void setFall(int fall) {
        this.fall = fall;
    }

    public String getCId() {
        return cId;
    }

    public void setCId(String cId) {
        this.cId = cId;
    }

    public BmobDate getDate() {
        return date;
    }

    public void setDate(BmobDate date) {
        this.date = date;
    }
}
