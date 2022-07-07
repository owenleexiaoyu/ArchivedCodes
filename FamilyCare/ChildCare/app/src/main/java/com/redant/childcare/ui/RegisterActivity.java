package com.redant.childcare.ui;

/**
 * Created by lenovo on 2016/12/6.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.redant.childcare.R;
import com.redant.childcare.fragments.FragmentRegistCheckcode;
import com.redant.childcare.fragments.FragmentRegistPass;


public class RegisterActivity extends BaseActivity{


    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Fragment checkFragment = new FragmentRegistCheckcode();
//        Fragment checkFragment = new FragmentRegistPass();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,checkFragment);
        transaction.commit();
    }
}
