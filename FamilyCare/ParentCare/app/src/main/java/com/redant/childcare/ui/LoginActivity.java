package com.redant.childcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.redant.childcare.R;
import com.redant.childcare.entry.MyUser;
import com.redant.childcare.entry.UserInfo;
import com.redant.childcare.utils.AppConfig;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;


import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by 啸宇 on 2016/12/15.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_login,btn_to_register,btn_forgetpass;
    private EditText etPhone,etPassword;//输入手机号和密码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        btn_login = (Button) findViewById(R.id.login_login);
        btn_to_register = (Button) findViewById(R.id.login_register);
        btn_forgetpass = (Button) findViewById(R.id.login_forgetpass);
        etPhone = (EditText) findViewById(R.id.login_edit_phone);
        etPassword = (EditText) findViewById(R.id.login_edit_password);
        btn_login.setOnClickListener(this);
        btn_to_register.setOnClickListener(this);
        btn_forgetpass.setOnClickListener(this);
        btn_forgetpass.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_login:
                String phone = etPhone.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){
                    MyUser parentUser = new MyUser();
                    parentUser.setUsername(phone);
                    parentUser.setPassword(password);
                    parentUser.login(new SaveListener<MyUser>() {
                        @Override
                        public void done(MyUser myUser, BmobException e) {
                            if(e == null){
                                //登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                //登录失败
                                Toast.makeText(LoginActivity.this, "登录失败,请稍后再试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.login_register:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                break;
            case R.id.login_forgetpass:
                Toast.makeText(LoginActivity.this, "忘记密码了", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
