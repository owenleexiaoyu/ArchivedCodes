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
    //ListView?????????
    private ListView listView;
    private MyAdapter adapter;
    private List<DaliyData> dataList;

    //????????????
    private FloatingActionButton fab;
    //??????????????????????????????
    private TextView tvNickname;
    private CircleImageView imgIcon;
    //??????????????????????????????
    List<MyUser> linkList;

    private String phoneNumber = "";//?????????????????????

    private MyUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????MobSDK
        MobSDK.init(this, AppConfig.SMSSDK_APP_KEY, AppConfig.SMSSDK_APP_SECRET);
        //??????????????????
        Connector.getDatabase();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //????????????
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        //NavigationView?????????????????????????????????
        tvNickname = (TextView) headerView.findViewById(R.id.main_tv_nickname);
        imgIcon = (CircleImageView) headerView.findViewById(R.id.main_icon);
        //???????????????
        initData();
        //???????????????
        initView();
        //????????????????????????????????????????????????????????????
        initNickNameAndLink();
        //?????????adapter
        adapter = new MyAdapter(MainActivity.this, dataList);
        //ListView??????adapter
        listView.setAdapter(adapter);
        //????????????????????????
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
                builder.setTitle("??????")
                        .setMessage("????????????????????????")
                        .setCancelable(true)
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //????????????
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
        //FloatingActionButton???????????????
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linkList == null || linkList.size() == 0) {
                    //?????????????????????
                    Toast.makeText(MainActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    showLinkListDialog();
                }
            }
        });
    }


    /**
     * ?????????????????????????????????
     */
    private void showLinkListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????????????????");
        //???list???????????????????????????
        String[] links = new String[linkList.size()];
        for (int i = 0; i < linkList.size(); i++) {
            MyUser index = linkList.get(i);
            links[i] = index.getNickName() + "(" + index.getUsername() + ")";
        }
        builder.setItems(links, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "???????????????" + linkList.get(which).getPhoneNumber(),
//                        Toast.LENGTH_SHORT).show();
                phoneNumber = linkList.get(which).getUsername();
                //??????????????????????????????
                requestCallPermision(phoneNumber);
            }
        });
        builder.show();
    }

    /**
     * ????????????
     * @param phoneNumber
     */
    private void requestCallPermision(String phoneNumber) {
        //??????Android??????????????????23
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
     * ??????????????????
     * @param phoneNumber
     */
    private void call(String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(intent);
    }

    private void initData() {
        //?????????????????????????????????
        dataList = DataSupport.findAll(DaliyData.class);
        Collections.reverse(dataList);
    }

    private void initView() {
        //?????????ListView
        listView = (ListView) findViewById(R.id.main_listview);
        //?????????????????????
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
                Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
        } else {
            //??????????????????
            tvNickname.setText(currentUser.getNickName());
            //??????????????????
            Picasso.with(this).load(currentUser.getIconUrl()).into(imgIcon);
            //???????????????????????????????????????
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
     * ?????????????????????????????????
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
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    /**
     * ??????????????????????????????????????????
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
                    //??????
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
//                    Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "?????????"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
