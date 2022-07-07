package com.redant.childcare.model;

import cn.bmob.v3.BmobUser;

/**
 * 儿童的实体类，用于保存儿童的个人信息
 * Created by 啸宇 on 2017-10-26.
 */

public class MyUser extends BmobUser {

    private String nickName;//用户昵称
    private int age;//用户年龄
    private boolean gender;//性别，true 是男，false 是女
    private String iconUrl;//用户头像
    private String link1;//绑定的第一个家长或儿童
    private String link2;//绑定的第二个家长或儿童
    private boolean flag;//判断是家长或者是儿童，true是儿童，false是家长

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLink1() {
        return link1;
    }

    public void setLink1(String link1) {
        this.link1 = link1;
    }

    public String getLink2() {
        return link2;
    }

    public void setLink2(String link2) {
        this.link2 = link2;
    }

}
