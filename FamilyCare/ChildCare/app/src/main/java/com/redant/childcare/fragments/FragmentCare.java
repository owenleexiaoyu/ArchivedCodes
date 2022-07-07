package com.redant.childcare.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.redant.childcare.R;
import com.redant.childcare.ui.ChartActivity;
import com.redant.childcare.ui.MapActivity;
import com.redant.childcare.services.FallCountDetector;
import com.redant.childcare.services.FallService;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;


public class FragmentCare extends Fragment implements View.OnClickListener {

    private TextView tvFallTime;//跌到次数
    private Button btnEnterMap;//进入地理围栏

    private int fallTimes = 0;//跌倒次数
    private int tempFall = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(tempFall >= fallTimes){
                fallTimes = FallCountDetector.FALLCOUNTS;
            }
            else{
                if(FallCountDetector.FALLCOUNTS > tempFall){
                    fallTimes += (FallCountDetector.FALLCOUNTS - tempFall);
                }
            }
            tempFall = FallCountDetector.FALLCOUNTS;
            Log.i("FragmentCare","fallTimes----"+fallTimes);
            Log.i("FragmentCare","tempFall  >>>"+FallCountDetector.FALLCOUNTS);
            tvFallTime.setText(fallTimes + "");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care, null);
        initData();
        //初始化控件
        initView(view);
        //启动跌倒的后台服务
        Intent fallServiceIntent = new Intent(getActivity(), FallService.class);
        getActivity().startService(fallServiceIntent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(900);
                        Message msg = Message.obtain();
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return view;
    }

    private void initData() {
        //从数据库中获取保存的跌倒次数
        fallTimes = ShareUtils.getInt(getActivity(), UtilTools.getdateFormatKey2(), 0);
    }

    private void initView(View view) {
        tvFallTime = (TextView) view.findViewById(R.id.f_care_tv_falltime);
        //设置初始值
        tvFallTime.setText(fallTimes + "");
        tvFallTime.setOnClickListener(this);
        btnEnterMap = (Button) view.findViewById(R.id.f_care_btn_entermap);
        btnEnterMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.f_care_btn_entermap:
                startActivity(new Intent(getActivity(), MapActivity.class));
                break;
            case R.id.f_care_tv_falltime:
                startActivity(new Intent(getActivity(), ChartActivity.class));
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //保存跌倒数据
        ShareUtils.putInt(getActivity(),UtilTools.getdateFormatKey2(),fallTimes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存跌倒数据
        ShareUtils.putInt(getActivity(),UtilTools.getdateFormatKey2(),fallTimes);
    }
}