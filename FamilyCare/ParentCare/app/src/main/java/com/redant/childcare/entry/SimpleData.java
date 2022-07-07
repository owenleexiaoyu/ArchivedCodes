package com.redant.childcare.entry;

/**
 * Created by 啸宇 on 2017-07-07.
 */

public class SimpleData {
    private String name;
    private String phoneNumber;

    public SimpleData(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
