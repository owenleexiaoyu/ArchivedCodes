package com.redant.childcare.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kymjs.rxvolley.client.HttpParams;
import com.redant.childcare.model.MyUser;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.R;
import com.redant.childcare.widgets.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2017/3/22.
 */

public class FragmentRegistPass extends Fragment {
    private EditText etNickName;
    private EditText etPass;
    private EditText etPassAgain;
    private String flag = null;
    private static final String TAG = "FragmentRegistPass";
    private Button btnRegist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regist_pass,container,false);
        initView(view);
        return view;
    }

    private void regist() {
        //-----------用于注册测试用--------
//        AppConfig.REGIST_USER_NAME = ((int)(Math.random()*100000))+"";
        //--------------------------------
        Log.i(TAG,"USER_NAME="+AppConfig.REGIST_USER_NAME);
        String nickName = etNickName.getText().toString();
        String password = etPass.getText().toString();
        String passAgain = etPassAgain.getText().toString();
        if (!TextUtils.isEmpty(nickName) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(passAgain)){
            if (password.equals(passAgain)){
                Log.i(TAG,AppConfig.REGIST_USER_NAME);
                MyUser childUser = new MyUser();
                childUser.setUsername(AppConfig.REGIST_USER_NAME);
                childUser.setNickName(nickName);
                childUser.setFlag(true);
                childUser.setPassword(password);
                //设置默认属性
                childUser.setAge(0);
                childUser.setIconUrl("");
                childUser.setGender(true);
                childUser.setLink1("");
                childUser.setLink2("");
                //注册
                childUser.signUp(new SaveListener<MyUser>() {
                    @Override
                    public void done(MyUser myUser, BmobException e) {
                        if(e == null){
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }else{
                            Toast.makeText(getActivity(), "注册失败,请稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else{
                Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "输入不能为空", Toast.LENGTH_SHORT).show();
        }
    }
    private void initView(View view) {
        etNickName = (EditText) view.findViewById(R.id.regist_edit_nickname);
        etPass = (EditText) view.findViewById(R.id.regist_edit_password);
        etPassAgain = (EditText) view.findViewById(R.id.regist_edit_password_again);
        btnRegist = (Button) view.findViewById(R.id.regist_regist);
        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regist();
            }
        });
    }
}
