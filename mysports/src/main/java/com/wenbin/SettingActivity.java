package com.wenbin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wenbin.mysports.R;


/**
 * Created by wenbin on 2016-03-08.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CALORIE_GOAL = "caloriegoal";
    public static final String KEY_CALORIE_GOAL = "keycaloriegoal";
    public static final String KEY_INFO_AGE = "infoage";
    public static final String KEY_INFO_SEX_MALE = "infosexmale";
    public static final String KEY_INFO_HEIGHT = "infoheight";
    public static final String KEY_INFO_WEIGHT= "infoweight";
    private LinearLayout setCalorieGoal;
    private TextView tv_calorie_goal;
    private RelativeLayout sexlayout;
    private RelativeLayout agelayout;
    private RelativeLayout heightlayout;
    private RelativeLayout weightlayout;
    private TextView tvsex;
    private TextView tvage;
    private TextView tvheight;
    private TextView tvweight;
    private SharedPreferences sp;
    private Button btnCreateGoal;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initView();
    }

    private void initView() {
        setCalorieGoal = (LinearLayout) findViewById(R.id.set_caloriegoal);
        tv_calorie_goal = (TextView) findViewById(R.id.goal_tv_calorie);
        setCalorieGoal.setOnClickListener(this);
        sexlayout = (RelativeLayout) findViewById(R.id.info_layout_sex);
        agelayout = (RelativeLayout) findViewById(R.id.info_layout_age);
        heightlayout = (RelativeLayout) findViewById(R.id.info_layout_height);
        weightlayout = (RelativeLayout) findViewById(R.id.info_layout_weight);
        sexlayout.setOnClickListener(this);
        agelayout.setOnClickListener(this);
        heightlayout.setOnClickListener(this);
        weightlayout.setOnClickListener(this);

        sp = getSharedPreferences(CALORIE_GOAL, Context.MODE_PRIVATE);

        tvsex = (TextView) findViewById(R.id.info_tv_sex);
        tvage = (TextView) findViewById(R.id.info_tv_age);
        tvheight = (TextView) findViewById(R.id.info_tv_height);
        tvweight = (TextView) findViewById(R.id.info_tv_weight);
        tv_calorie_goal.setText(sp.getString(KEY_CALORIE_GOAL, "2000") + "卡");
        tvage.setText(sp.getString(KEY_INFO_AGE, "20"));
        tvheight.setText(sp.getString(KEY_INFO_HEIGHT, "170")+"cm");
        tvweight.setText(sp.getString(KEY_INFO_WEIGHT, "50")+"kg");
        tvsex.setText(sp.getBoolean(KEY_INFO_SEX_MALE,true)?"男":"女");

        btnCreateGoal = (Button) findViewById(R.id.btn_create_caloriegoal);
        btnCreateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double goal;
                if(sp.getBoolean(KEY_INFO_SEX_MALE,true)){
                    goal = Math.floor((66+(13.8*Float.valueOf(sp.getString(KEY_INFO_WEIGHT,"50")))+
                            (5*Float.valueOf(sp.getString(KEY_INFO_HEIGHT,"170")))-
                            (6.8*Integer.valueOf(sp.getString(KEY_INFO_AGE,"20"))))*1.55);
                }else{
                    goal = Math.floor((655+(9.6*Float.valueOf(sp.getString(KEY_INFO_WEIGHT,"50")))+
                            (1.8*Float.valueOf(sp.getString(KEY_INFO_HEIGHT,"170")))-
                            (4.7*Integer.valueOf(sp.getString(KEY_INFO_AGE,"20"))))*1.55);
                }
                Toast.makeText(SettingActivity.this,"你的卡路里目标为："+goal+"卡",Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor e = sp.edit();
                e.putString(KEY_CALORIE_GOAL, goal+"");
                e.commit();
                tv_calorie_goal.setText(sp.getString(KEY_CALORIE_GOAL, "2000") + "卡");
            }
        });
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.dialog_layout, null);

        final EditText input = (EditText) v.findViewById(R.id.dialog_input);
        Button done = (Button) v.findViewById(R.id.dialog_input_done);
        AlertDialog dialog = null;

        switch (view.getId()) {
            case R.id.set_caloriegoal:
                builder.setView(v);
                input.setHint("输入你的卡路里消耗目标");
                dialog = builder.create();
                dialog.show();
                final AlertDialog finalDialog = dialog;
                done.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        String goalNum = input.getText().toString();
                        SharedPreferences.Editor e = sp.edit();
                        e.putString(KEY_CALORIE_GOAL, goalNum);
                        e.commit();
                        tv_calorie_goal.setText(sp.getString(KEY_CALORIE_GOAL, "2000") + "卡");
                        finalDialog.dismiss();
                    }
                });
                break;
            case R.id.info_layout_age:
                builder.setView(v);
                input.setHint("输入你的年龄");
                dialog = builder.create();
                dialog.show();
                final AlertDialog finalDialog1 = dialog;
                done.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        String goalNum = input.getText().toString();
                        SharedPreferences.Editor e = sp.edit();
                        e.putString(KEY_INFO_AGE, goalNum);
                        e.commit();
                        tvage.setText(sp.getString(KEY_INFO_AGE, "20"));
                        finalDialog1.dismiss();
                    }
                });
                break;
            case R.id.info_layout_height:
                builder.setView(v);
                input.setHint("输入你的身高（cm）");
                dialog = builder.create();
                dialog.show();
                final AlertDialog finalDialog2 = dialog;
                done.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        String goalNum = input.getText().toString();
                        SharedPreferences.Editor e = sp.edit();
                        e.putString(KEY_INFO_HEIGHT, goalNum);
                        e.commit();
                        tvheight.setText(sp.getString(KEY_INFO_HEIGHT, "170")+"cm");
                        finalDialog2.dismiss();
                    }
                });
                break;
            case R.id.info_layout_weight:
                builder.setView(v);
                input.setHint("输入你的体重（kg）");
                dialog = builder.create();
                dialog.show();
                final AlertDialog finalDialog3 = dialog;
                done.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        String goalNum = input.getText().toString();
                        SharedPreferences.Editor e = sp.edit();
                        e.putString(KEY_INFO_WEIGHT, goalNum);
                        e.commit();
                        tvweight.setText(sp.getString(KEY_INFO_WEIGHT, "50")+"kg");
                        finalDialog3.dismiss();
                    }
                });
                break;
            case R.id.info_layout_sex:
                builder.setSingleChoiceItems(new String[]{"男", "女"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean(KEY_INFO_SEX_MALE,true);
                            e.commit();
                        }else{
                            SharedPreferences.Editor e = sp.edit();
                            e.putBoolean(KEY_INFO_SEX_MALE,false);
                            e.commit();
                        }
                        tvsex.setText(sp.getBoolean(KEY_INFO_SEX_MALE,true)?"男":"女");
                        dialogInterface.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
    }
}
