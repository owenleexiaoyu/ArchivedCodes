package com.redant.childcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.redant.childcare.R;
import com.redant.childcare.entry.DaliyData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by 啸宇 on 2017-07-06.
 */

public class ItemContentActivity extends BaseActivity{
    public static final String EXTRA_KEY_STEP = "extra_key_step";
    public static final String EXTRA_KEY_FALL = "extra_key_fall";
    public static final String EXTRA_KEY_DATE = "extra_key_date";
    public static final String EXTRA_KEY_NAME = "extra_key_name";
    private TextView tvStep, tvFall, tvClass;
    private int mStep,mFall;
    private String mName;
    private int mClass;
    private static final String TAG = "ItemContentActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_content);
        Intent intent = getIntent();
        mStep = intent.getIntExtra(EXTRA_KEY_STEP, 0);
        mFall = intent.getIntExtra(EXTRA_KEY_FALL,0);
        mName = intent.getStringExtra(EXTRA_KEY_NAME);
        Log.i(TAG, "onCreate: step="+mStep+" fall="+mFall+" name="+mName);
        mClass = getStepClass();
        initView();
    }

    /**
     * 获取到今天的步数在近七天内的排名
     */
    private int getStepClass() {
        List<DaliyData> list = DataSupport
                .where("childName=?",mName)
                .limit(7)
                .order("date desc")
                .find(DaliyData.class);
        List<Integer> stepList = new ArrayList<>();
        for (DaliyData d:
             list) {
            stepList.add(d.getStep());
            Log.i(TAG, "getStepClass: datalist index = "+d.toString());
        }
        Log.i(TAG, "getStepClass: size of steplist = "+stepList.size());
        Collections.sort(stepList);
        Collections.reverse(stepList);
        for (Integer i :
                stepList) {
            Log.i(TAG, "getStepClass: index = "+(int)i);
        }
        return stepList.indexOf(new Integer(mStep))+1;
    }

    private void initView() {
        tvStep = (TextView) findViewById(R.id.content_tv_step);
        tvFall = (TextView) findViewById(R.id.content_tv_fall);
        tvClass = (TextView) findViewById(R.id.content_tv_class);
        tvStep.setText(mStep+"");
        tvFall.setText(mFall+"");
        tvClass.setText(mClass+"");
    }
}
