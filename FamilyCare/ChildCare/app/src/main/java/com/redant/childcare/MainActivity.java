package com.redant.childcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.redant.childcare.fragments.FragmentCare;
import com.redant.childcare.fragments.FragmentMine;
import com.redant.childcare.fragments.FragmentSport;
import com.redant.childcare.services.AlarmService;
import com.redant.childcare.utils.AlarmManagerUtil;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.widgets.NoScrollViewPager;
import com.redant.jptabbar.JPTabBar;
import com.redant.jptabbar.OnTabSelectListener;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.SMSSDK;

import static com.redant.childcare.R.id.tabbar;


public class MainActivity extends AppCompatActivity implements OnTabSelectListener{

    private List<Fragment> list = new ArrayList<>();
    private NoScrollViewPager mPager;
    private JPTabBar mTabbar;
    private int [] fragment_titleId = {R.string.tab1,R.string.tab2,R.string.tab4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        SMSSDK.initSDK(this, AppConfig.SMSSDK_APP_ID,AppConfig.SMSSDK_APP_SECRET);
        //初始化数据库
        Connector.getDatabase();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(fragment_titleId[0]);

        mPager = (NoScrollViewPager) findViewById(R.id.view_pager);
        mTabbar = (JPTabBar) findViewById(tabbar);
        mTabbar.setTitles(fragment_titleId[0],fragment_titleId[1],fragment_titleId[2])
                .setNormalIcons(R.mipmap.tab_sport_normal, R.mipmap.tab_map_normal, R.mipmap.tab_mine_normal)
                .setSelectedIcons(R.mipmap.tab_sport_checked, R.mipmap.tab_map_checked, R.mipmap.tab_mine_checked)
                .generate();
        mPager.setNoScroll(false);
        list.add(new FragmentSport());
        list.add(new FragmentCare());
        list.add(new FragmentMine());
        mPager.setAdapter(new com.redant.childcare.Adapter(getSupportFragmentManager(), list));
        //设置容器
        mTabbar.setContainer(mPager);
        mTabbar.setTabListener(this);
        //设置定时任务，上传运动数据
        AlarmManagerUtil.setAlarm(this, 1, 20, 0, 0, 0,"定时任务，上传数据", 0);
    }




    @Override
    public void onTabSelect(int index) {
        getSupportActionBar().setTitle(fragment_titleId[index]);
    }

    @Override
    public void onClickMiddle(View middleBtn) {
    }

}
