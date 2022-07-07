package com.redant.childcare.entry;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by 啸宇 on 2017-07-06.
 */

public class DaliyData extends DataSupport {
    private long id;
    private String childName;
    private Date date;
    private int step;
    private int fall;

    public long getId() {
        return id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    @Override
    public String toString() {
        return "[" + childName + " " + step + " " + fall + "]";
    }
}
