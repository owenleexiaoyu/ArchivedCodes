package com.redant.childcare.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.R;
import com.redant.childcare.utils.Logging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by Administrator on 2017/3/22.
 */

public class FragmentRegistCheckcode extends Fragment implements View.OnClickListener {

    private EditText etPhone;
    private EditText etCheckcode;
    private Button btnGetCode;
    private Button btnCheck;
    private int lefttime = 60;
    private Timer timer;
    private static final String TAG = "FragmentRegistCheckcode";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regist_checkcode,container,false);
        etPhone = (EditText) view.findViewById(R.id.regist_edit_phone);
        etCheckcode = (EditText) view.findViewById(R.id.regist_edit_checkcode);
        btnGetCode = (Button) view.findViewById(R.id.regist_btn_getcode);
        btnCheck = (Button) view.findViewById(R.id.regist_check);
        btnGetCode.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        String phoneNum = etPhone.getText().toString();
        switch (view.getId()){
            case R.id.regist_btn_getcode:
                if (TextUtils.isEmpty(phoneNum)){
                    Toast.makeText(getActivity(), "请输入手机号", Toast.LENGTH_SHORT).show();
                }else {
                    //发送验证码
                    SMSSDK.getVerificationCode("86", phoneNum);
                    //将按钮设置为不可用状态
//                    btnGetCode.setBackgroundColor(Color.parseColor("0xf0f0f0"));
                    btnGetCode.setText("60秒后重发");
                    btnGetCode.setEnabled(false);
                    btnGetCode.setBackground(getResources().getDrawable(R.drawable.commonbtnbg_uneabled));
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            lefttime--;
                            Message message = new Message();
                            message.what = 1;
                            myHandler.sendMessage(message);
                        }
                    },1000,1000);
                }
                break;
            case R.id.regist_check:
                String code = etCheckcode.getText().toString();
                if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(code)){
                    Toast.makeText(getActivity(), "请输入手机号或验证码", Toast.LENGTH_SHORT).show();
                }else {
                    SMSSDK.submitVerificationCode("86",phoneNum,code);
                }
        }
        
    }
    EventHandler handler = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Logging.d(TAG, "afterEvent: ----------------------验证成功了---------------");
                    Message msg = new Message();
                    msg.what = 2;
                    myHandler.sendMessage(msg);
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    boolean smart = (boolean) data;
                    if (smart){
                        //智能验证
                    }else{
                        //依然短信验证
                    }
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            }else{
                ((Throwable)data).printStackTrace();
                try {
                    JSONObject obj = new JSONObject((String) data);
                    String detail = (String) obj.get("detail");
                    Logging.d(TAG, "afterEvent: detail = " + detail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    btnGetCode.setText(lefttime + "秒后重发");
                    if(lefttime < 0){
                        timer.cancel();
                        btnGetCode.setEnabled(true);
                        btnGetCode.setText("获取验证码");
                        btnGetCode.setBackground(getResources().getDrawable(R.drawable.commonbtnbg_selector));
                    }
                    break;
                case 2:
                    Toast.makeText(getActivity(), "验证成功", Toast.LENGTH_SHORT).show();
                    AppConfig.REGIST_USER_NAME = etPhone.getText().toString();
                    FragmentManager manager = getFragmentManager();
                    Fragment passFragment = new FragmentRegistPass();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.container,passFragment);
                    transaction.commit();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        SMSSDK.registerEventHandler(handler);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
        SMSSDK.unregisterEventHandler(handler);
    }

}
