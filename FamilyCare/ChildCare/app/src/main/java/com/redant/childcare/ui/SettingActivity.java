package com.redant.childcare.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.redant.childcare.R;
import com.redant.childcare.model.MyUser;
import com.redant.childcare.model.SportData;
import com.redant.childcare.model.UserInfo;
import com.redant.childcare.services.AlarmService;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by jpeng on 16-11-25.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvLinkTip;
    private LinearLayout llLink1, llLink2;
    private TextView tvLink1, tvLink2, tvGoal;
    private Button btnNote1, btnNote2, btnEditGoal;
    private Button btnUpdate, btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        tvLinkTip = (TextView) findViewById(R.id.setting_tv_link_tip);
        llLink1 = (LinearLayout) findViewById(R.id.setting_ll_link1);
        llLink2 = (LinearLayout) findViewById(R.id.setting_ll_link2);
        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser == null) {
            tvLinkTip.setText("未关联任何家长");
            llLink1.setVisibility(View.GONE);
            llLink2.setVisibility(View.GONE);
        } else {
            //如果没关联，则不显示
            if (TextUtils.isEmpty(currentUser.getLink1())) {
                llLink1.setVisibility(View.GONE);
            } else {
                tvLink1 = (TextView) findViewById(R.id.setting_tv_link1);
                tvLink1.setText(currentUser.getLink1());
                btnNote1 = (Button) findViewById(R.id.setting_btn_note1);
                btnNote1.setText(ShareUtils.getString(this, currentUser.getLink1(),
                        getString(R.string.no_note)));
                btnNote1.setOnClickListener(this);
            }
            if (TextUtils.isEmpty(currentUser.getLink2())) {
                llLink2.setVisibility(View.GONE);
            } else {
                tvLink2 = (TextView) findViewById(R.id.setting_tv_link2);
                tvLink2.setText(currentUser.getLink2());
                btnNote2 = (Button) findViewById(R.id.setting_btn_note2);
                btnNote2.setText(ShareUtils.getString(this, currentUser.getLink2(),
                        getString(R.string.no_note)));
                btnNote2.setOnClickListener(this);
            }
        }
        tvGoal = (TextView) findViewById(R.id.setting_tv_goal);
        int goal = ShareUtils.getInt(this, AppConfig.SHARED_KEY_PROGRESS_MAX, 5000);
        tvGoal.setText("每日" + goal + "步");
        btnEditGoal = (Button) findViewById(R.id.setting_btn_goal_edit);
        btnUpdate = (Button) findViewById(R.id.setting_btn_update);
        btnLogout = (Button) findViewById(R.id.setting_btn_logout);
        btnEditGoal.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //上传每日数据
            case R.id.setting_btn_update:
                int steps = ShareUtils.getInt(this, UtilTools.getdateFormatKey1(), 0);
                int falls = ShareUtils.getInt(this, UtilTools.getdateFormatKey2(), 0);
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                if (user != null) {
                    update(steps, falls);
                } else {
                    Toast.makeText(this, "未登录，请登录后发送", Toast.LENGTH_SHORT).show();
                }
                break;
            //退出登录
            case R.id.setting_btn_logout:
                BmobUser.logOut();
                finish();
                break;
            case R.id.setting_btn_note1:
                //修改备注
                setNoteFromDialog(0);
                break;
            case R.id.setting_btn_note2:
                setNoteFromDialog(1);
                break;
            case R.id.setting_btn_goal_edit:
                //修改每日运动目标
                editGoal();
                break;
        }
    }

    private void editGoal() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_note, null);
        builder.setView(dialogView);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.note_tv_title);
        tvTitle.setText("每日目标");
        final EditText etGoal = (EditText) dialogView.findViewById(R.id.note_et_notename);
        Button btnChange = (Button) dialogView.findViewById(R.id.note_btn_change);
        final AlertDialog dialog = builder.create();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalStr = etGoal.getText().toString();
                if (!TextUtils.isEmpty(goalStr)) {
                    try {
                        int goal = Integer.parseInt(goalStr);
                        ShareUtils.putInt(SettingActivity.this, AppConfig.SHARED_KEY_PROGRESS_MAX, goal);
                        tvGoal.setText("每日" + goal + "步");
                    } catch (Exception e) {
                        Toast.makeText(SettingActivity.this, "修改失败，请重试", Toast.LENGTH_SHORT).show();
                    } finally {
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * 测试发送运动数据方法
     * @param steps
     * @param falls
     */
    private void update(int steps, int falls) {
//        SportData data = new SportData();
//        data.setStep(steps);
//        data.setFall(falls);
//        data.setCId(BmobUser.getCurrentUser().getObjectId());
//        data.setDate(new BmobDate(new Date()));
//        data.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    //上传成功
//                    Toast.makeText(SettingActivity.this, "上传成功:" + s,
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    //上传失败
//                    Toast.makeText(SettingActivity.this, "上传失败:" + s,
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        String data = "{\n" +
                "    \"code\": 0,\n" +
                "    \"msg\": {\n" +
                "        \"child\": "+BmobUser.getCurrentUser(MyUser.class).getNickName()+",\n" +
                "        \"step\": "+steps+",\n" +
                "        \"fall\": "+falls+"\n" +
                "    }\n" +
                "}";
        JSONObject object = null;
        try {
            object = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        List<String> channels = new ArrayList<>();
        //TODO 替换成你需要推送的所有频道，推送前请确认已有设备订阅了该频道，也就是channels属性存在该值
        channels.add(BmobUser.getCurrentUser().getObjectId());
        query.addWhereContainedIn("channels", channels);
        bmobPushManager.setQuery(query);
        bmobPushManager.pushMessage(object, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    Log.i("TAG","推送成功！");
                    Toast.makeText(SettingActivity.this, "发送数据成功", Toast.LENGTH_SHORT).show();
                }else {
                    Log.e("TAG","异常：" + e.getMessage());
                    Toast.makeText(SettingActivity.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setNoteFromDialog(final int index) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_note, null);
        builder.setView(dialogView);
        final EditText etNote = (EditText) dialogView.findViewById(R.id.note_et_notename);
        Button btnChange = (Button) dialogView.findViewById(R.id.note_btn_change);
        final AlertDialog dialog = builder.create();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteName = etNote.getText().toString();
                if (!TextUtils.isEmpty(noteName)) {
                    switch (index) {
                        case 0:
                            ShareUtils.putString(SettingActivity.this,
                                    tvLink1.getText().toString(), noteName);
                            btnNote1.setText(noteName);
                            break;
                        case 1:
                            ShareUtils.putString(SettingActivity.this,
                                    tvLink2.getText().toString(), noteName);
                            btnNote2.setText(noteName);
                            break;
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    /**
     * 测试发送示警信号方法
     * @param view
     * @throws JSONException
     */
    public void sendWarning(View view) throws JSONException {
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        List<String> channels = new ArrayList<>();
        //TODO 替换成你需要推送的所有频道，推送前请确认已有设备订阅了该频道，也就是channels属性存在该值
        channels.add(BmobUser.getCurrentUser().getObjectId());
        query.addWhereContainedIn("channels", channels);
        bmobPushManager.setQuery(query);
        String data = "{\n" +
                "    \"code\": 1,\n" +
                "    \"msg\": {\n" +
                "        \"child\": "+BmobUser.getCurrentUser(MyUser.class).getNickName()+"\n" +
                "    }\n" +
                "}";
        JSONObject object = new JSONObject(data);
        bmobPushManager.pushMessage(object, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("TAG", "推送成功！");
                    Toast.makeText(SettingActivity.this, "发送示警信号成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TAG", "异常：" + e.getMessage());
                    Toast.makeText(SettingActivity.this, "发送示警信号失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
