package com.redant.childcare.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.redant.childcare.R;
import com.redant.childcare.fragments.FragmentChart;
import com.redant.childcare.fragments.FragmentSport;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-05-20.
 */

public class ChartActivity extends BaseActivity {
    //测试数据
    private String [] stepKeys = new String [7];          //步数的key
    private String [] fallKeys = new String [7];          //跌倒的key
    private String [] within7days = new String [7];       //x轴的日期标注
    private double [] stepIn7days =new double[7];
    private double [] calroieIn7days =new double[7];
    private double [] fallIn7days =new double[7];
    //控件
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    //数据源
    private List<String> titles;
    private List<Fragment> fragmentList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //去除actionbar的阴影
        getSupportActionBar().setElevation(0);
        initData();
        mTabLayout = (TabLayout) findViewById(R.id.chart_tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.chart_viewPager);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        //将tablayout 与viewpager进行绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //填充tab的标题
        titles = new ArrayList<>();
        titles.add("步数");
        titles.add("卡路里");
        titles.add("跌倒");

        //获取今天步数和跌倒的key
        stepKeys[6] = UtilTools.getdateFormatKey1();
        fallKeys[6] = UtilTools.getdateFormatKey2();
        //填充X轴标注的文字的数据源
        within7days[6] = "今天";
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i < 7; i++) {
            Date date = getPreviousDate(new Date(),i);
            within7days[6 - i] = format1.format(date);
            //获取过去六天的步数和跌倒的key
            stepKeys[6 - i] = format2.format(date);
            fallKeys[6 - i] = format3.format(date);
        }

        //填充数据源
        for (int i = 0; i < 7; i++) {
            stepIn7days[i] = ShareUtils.getInt(this, stepKeys[i], 0);
            fallIn7days[i] = ShareUtils.getInt(this,fallKeys[i], 0);
            calroieIn7days[i] = FragmentSport.calculateCalorie((int) stepIn7days[i]);
        }

        //填充viewpager的三个fragment
        fragmentList = new ArrayList<>();

        //为第一个fragment填充数据
        Bundle bundle1 = new Bundle();
        bundle1.putString("chartName","近7天步数统计");
        bundle1.putString("yLabel","步数（步）");
        bundle1.putStringArray("xLabels",within7days);
        bundle1.putDoubleArray("dataValues",stepIn7days);
        FragmentChart frag1 = new FragmentChart();
        frag1.setArguments(bundle1);
        fragmentList.add(frag1);

        //为第二个fragment填充数据
        Bundle bundle2 = new Bundle();
        bundle2.putString("chartName","近7天热量统计");
        bundle2.putString("yLabel","卡路里（卡）");
        bundle2.putStringArray("xLabels",within7days);
        bundle2.putDoubleArray("dataValues",calroieIn7days);
        FragmentChart frag2 = new FragmentChart();
        frag2.setArguments(bundle2);
        fragmentList.add(frag2);

        //为第三个fragment填充数据
        Bundle bundle3 = new Bundle();
        bundle3.putString("chartName","近7天跌倒统计");
        bundle3.putString("yLabel","跌倒次数（次）");
        bundle3.putStringArray("xLabels",within7days);
        bundle3.putDoubleArray("dataValues",fallIn7days);
        FragmentChart frag3 = new FragmentChart();
        frag3.setArguments(bundle3);
        fragmentList.add(frag3);
    }
    /**
     * 获取前i天的日期
     * @param date
     * @param i
     * @return
     */
    private Date getPreviousDate(Date date, int i){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH,0 - i);
        return c.getTime();
    }
    /**
     * ViewPager的适配器
     */
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        //获取tab的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
