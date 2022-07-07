package com.redant.childcare.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.redant.childcare.R;

/**
 * Created by Administrator on 2017-06-01.
 */

public class AboutActivity extends BaseActivity{
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String [] data = {"关于红蚂蚁","意见反馈","版本更新"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        listView = (ListView) findViewById(R.id.about_listview);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
