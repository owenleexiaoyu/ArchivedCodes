package com.wenbin.mysports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wenbin.view.BarChart;

public class Old extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BarChart().getView(getApplicationContext()));
    }
}
