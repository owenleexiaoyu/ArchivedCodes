package com.redant.childcare.app;

import android.app.Application;
import android.util.Log;

import com.redant.childcare.utils.AppConfig;

import org.litepal.LitePal;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by 啸宇 on 2017-10-26.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bmob SDK
        Bmob.initialize(getApplicationContext(), AppConfig.BMOB_APP_ID);
        //初始化 Litepal 数据库
        LitePal.initialize(this);
        // 使用推送服务时的初始化操作
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    Log.i("BMOBPUSH",bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Log.e("BMOBPUSH",e.getMessage());
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(getApplicationContext());
    }
}
