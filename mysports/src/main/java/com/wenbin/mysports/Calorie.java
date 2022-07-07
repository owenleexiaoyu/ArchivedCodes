package com.wenbin.mysports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wenbin.view.PieChart;

public class Calorie extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view  = new PieChart(this).getView();
        setContentView(view);
    }
}
