package com.redant.childcare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.childcare.adapters.MyAdapter;
import com.redant.childcare.entry.DaliyData;
import com.redant.childcare.entry.DaliyItem;
import com.redant.childcare.entry.MyUser;
import com.redant.childcare.entry.SimpleData;
import com.redant.childcare.entry.UserInfo;
import com.redant.childcare.ui.AboutActivity;
import com.redant.childcare.ui.InfoActivity;
import com.redant.childcare.ui.ItemContentActivity;
import com.redant.childcare.ui.LoginActivity;
import com.redant.childcare.ui.SettingActivity;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.utils.ShareUtils;
import com.mob.MobSDK;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    //ListView及相关
    private ListView listView;
    private MyAdapter adapter;
    private List<DaliyData> dataList;

    //悬浮按钮
    private FloatingActionButton fab;
    //侧滑菜单中的用户昵称
    private TextView tvNickname;
    private CircleImageView imgIcon;
    //家长所关联的所有子女
    List<MyUser> linkList;

    private String phoneNumber = "";//拨打的电话号码

    private MyUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //配置MobSDK
        MobSDK.init(this, AppConfig.SMSSDK_APP_KEY, AppConfig.SMSSDK_APP_SECRET);
        //初始化数据库
        Connector.getDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //默认生成
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        //NavigationView中的控件需要单独初始化
        tvNickname = (TextView) headerView.findViewById(R.id.main_tv_nickname);
        imgIcon = (CircleImageView) headerView.findViewById(R.id.main_icon);
        //初始化数据
        initData();
        //初始化控件
        initView();
        //设置用户昵称，获取家长关联的所有子女信息
        initNickNameAndLink();
        //初始化adapter
        adapter = new MyAdapter(MainActivity.this, dataList);
        //ListView设置adapter
        listView.setAdapter(adapter);
        //设置子项点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DaliyData daliyData = dataList.get(position);
                Intent intent = new Intent(MainActivity.this, ItemContentActivity.class);

                intent.putExtra(ItemContentActivity.EXTRA_KEY_STEP, daliyData.getStep());
                intent.putExtra(ItemContentActivity.EXTRA_KEY_FALL, daliyData.getFall());
                intent.putExtra(ItemContentActivity.EXTRA_KEY_NAME, daliyData.getChildName());
                view.getContext().startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示")
                        .setMessage("是否删除这条记录")
                        .setCancelable(true)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //删除数据
                                DataSupport.delete(DaliyData.class, dataList.get(position).getId());
                                dataList.remove(position);
                                adapter.setData(dataList);
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        //FloatingActionButton设置监听器
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkList == null || linkList.size() == 0) {
                    //没有关联的子女
                    Toast.makeText(MainActivity.this, "您尚未关联您的子女", Toast.LENGTH_SHORT).show();
                } else {
                    showLinkListDialog();
                }
            }
        });
    }


    /**
     * 显示家长关联的子女列表
     */
    private void showLinkListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("打电话给孩子");
        //将list中数据填充到数组中
        String[] links = new String[linkList.size()];
        for (int i = 0; i < linkList.size(); i++) {
            MyUser index = linkList.get(i);
            links[i] = index.getNickName() + "(" + index.getUsername() + ")";
        }
        builder.setItems(links, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "打电话给：" + linkList.get(which).getPhoneNumber(),
//                        Toast.LENGTH_SHORT).show();
                phoneNumber = linkList.get(which).getUsername();
                //动态申请打电话的权限
                requestCallPermision(phoneNumber);
            }
        });
        builder.show();
    }

    /**
     * 申请权限
     * @param phoneNumber
     */
    private void requestCallPermision(String phoneNumber) {
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);
            }
            else {
                call(phoneNumber);
            }
        }
        else {
            call(phoneNumber);
        }
    }

    /**
     * 打电话给子女
     * @param phoneNumber
     */
    private void call(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(intent);
    }

    private void initData() {
        //从本地数据库中获取数据
        dataList = DataSupport.findAll(DaliyData.class);
        Collections.reverse(dataList);
    }

    private void initView() {
        //初始化ListView
        listView = (ListView) findViewById(R.id.main_listview);
        //初始化悬浮按钮
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            if (currentUser == null) {
                Toast.makeText(this, "未登录，请登录后查看", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, InfoActivity.class));
            }
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_logout) {
            BmobUser.logOut();
            currentUser = null;
            initNickNameAndLink();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNickNameAndLink() {
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser == null) {
            Toast.makeText(this, "获取用户昵称失败", Toast.LENGTH_SHORT).show();
        } else {
            //设置用户昵称
            tvNickname.setText(currentUser.getNickName());
            //设置用户头像
            Picasso.with(this).load(currentUser.getIconUrl()).into(imgIcon);
            //获取家长关联的所有子女信息
            linkList = initLinkData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNickNameAndLink();
        initData();
        adapter.setData(dataList);
    }

    /**
     * 动态申请权限的回调函数
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(!TextUtils.isEmpty(phoneNumber)){
                        call(phoneNumber);
                    }
                }else{
                    Toast.makeText(this, "你拒绝了该权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * 加载绑定了这个家长账号的儿童
     */
    private List<MyUser> initLinkData() {
        linkList = new ArrayList<>();
        BmobQuery<MyUser> q1 = new BmobQuery<>();
        q1.addWhereEqualTo("link1", currentUser.getUsername());
        q1.addWhereEqualTo("flag", true);
        BmobQuery<MyUser> q2 = new BmobQuery<>();
        q2.addWhereEqualTo("link2", currentUser.getUsername());
        q2.addWhereEqualTo("flag", true);
        List<BmobQuery<MyUser>> qs = new ArrayList<>();
        qs.add(q1);
        qs.add(q2);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.or(qs);
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if(e == null){
                    //订阅
                    subscribeChildren(list);
                    linkList = list;

                }
                else{
                    e.printStackTrace();
                }
            }
        });
        return linkList;
    }
    private void subscribeChildren(List<MyUser> childList){
        List<String> channals = new ArrayList<>();
        for (MyUser u:
             childList) {
            channals.add(u.getObjectId());
        }
        BmobInstallationManager.getInstance().subscribe(channals, new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
//                    Toast.makeText(MainActivity.this, "批量订阅成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
