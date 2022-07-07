package com.redant.childcare.ui;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.redant.childcare.R;
import com.redant.childcare.entry.UserInfo;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

/**
 * Created by jpeng on 16-11-25.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvLinkTip;
    private LinearLayout llLink1, llLink2;
    private TextView tvLink1, tvLink2;
    private Button btnNote1, btnNote2;
    private Button btnDownLoad, btnLogout;//下载数据，退出登录
    private static final String TAG = "SettingActivity";

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
        UserInfo info = DataSupport.findLast(UserInfo.class);
        if (info == null) {
            tvLinkTip.setText("未关联任何家长");
            llLink1.setVisibility(View.GONE);
            llLink2.setVisibility(View.GONE);
        } else {
            //如果没关联，则不显示
            if (info.getLink1() == null) {
                llLink1.setVisibility(View.GONE);
            } else {
                tvLink1 = (TextView) findViewById(R.id.setting_tv_link1);
                tvLink1.setText(info.getLink1());
                btnNote1 = (Button) findViewById(R.id.setting_btn_note1);
                btnNote1.setText(ShareUtils.getString(this, info.getLink1(),
                        getString(R.string.no_note)));
                btnNote1.setOnClickListener(this);
            }
            if (info.getLink2() == null) {
                llLink2.setVisibility(View.GONE);
            } else {
                tvLink2 = (TextView) findViewById(R.id.setting_tv_link2);
                tvLink2.setText(info.getLink2());
                btnNote2 = (Button) findViewById(R.id.setting_btn_note2);
                btnNote2.setText(ShareUtils.getString(this, info.getLink2(),
                        getString(R.string.no_note)));
                btnNote2.setOnClickListener(this);
            }
        }
        btnDownLoad = (Button) findViewById(R.id.setting_btn_download);
        btnLogout = (Button) findViewById(R.id.setting_btn_logout);
        btnDownLoad.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //下载数据
            case R.id.setting_btn_download:
                UserInfo info = DataSupport.findLast(UserInfo.class);
                if (info == null) {
                    Toast.makeText(this, "尚未登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                //下载link1的运动信息
                String link1Name = info.getLink1();
                if (TextUtils.isEmpty(link1Name)) {
                    Toast.makeText(this, "未关联儿童", Toast.LENGTH_SHORT).show();
                    return;
                }
                download(link1Name);
                break;
            //退出登录
            case R.id.setting_btn_logout:
                //清空本地用户的数据缓存
                DataSupport.deleteAll(UserInfo.class);
                finish();
                break;
            case R.id.setting_btn_note1:
                //修改备注
                setNoteFromDialog(0);
                break;
            case R.id.setting_btn_note2:
                setNoteFromDialog(1);
                break;
        }
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
     * 从服务器下载每天的运动数据
     */
    private void download(final String cusername) {

        HttpParams params = new HttpParams();
        params.put("action", AppConfig.ACTION_DOWNLOAD);
        params.put("username", cusername);
        RxVolley.post(AppConfig.SERVICE_URL, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Toast.makeText(SettingActivity.this, "download=" + t, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject obj = new JSONObject(t);
                    boolean status = obj.getBoolean("status");
                    //下载数据成功
                    if (status) {
                        int steps = obj.getInt("steps");
                        int falltimes = obj.getInt("falltimes");
                        //保存数据
                        ShareUtils.putString(SettingActivity.this,
                                cusername + UtilTools.getdateFormatKey(),
                                steps + "@@" + falltimes);
                        //在通知栏进行显示通知
                        showNotificaion(cusername, steps, falltimes);
                    } else {
                        Toast.makeText(SettingActivity.this, "下载运动数据失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showNotificaion(String cusername, int steps, int falltimes) {
        //获取备注名
        String note = ShareUtils.getString(this, cusername, "你的孩子");
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, ItemContentActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("有新的运动数据了")
                .setContentText(note + "(" + cusername + ")" + "今天走了"
                        + steps + "步，跌倒了" + falltimes + "次")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.childcare_circle)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.childcare))
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }
}
