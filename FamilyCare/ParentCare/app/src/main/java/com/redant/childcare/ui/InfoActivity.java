package com.redant.childcare.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redant.childcare.R;
import com.redant.childcare.adapters.LinkAdapter;
import com.redant.childcare.entry.MyUser;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017-06-01.
 */

public class InfoActivity extends BaseActivity implements View.OnClickListener{
    private CircleImageView imgIcon;
    private EditText etPhone, etNickname, etAge;
    private Button btnSex, btnFinish;
    private TextView tvTip;
    private ListView mListView;
    private LinkAdapter adapter;
    private MyUser currentUser;
    //????????????????????????
    private List<MyUser> childList;
    private File pictureFile;
    private Uri imgUri;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new LinkAdapter(InfoActivity.this, childList);
            mListView.setAdapter(adapter);
            if(childList.size() == 0){
                tvTip.setText("???????????????");
            }
        }
    };
    private static final String TAG = "InfoActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();
    }


    private void initView() {
        //???????????????
        etPhone = (EditText) findViewById(R.id.info_ed_phone);
        etNickname = (EditText) findViewById(R.id.info_ed_name);
        etAge = (EditText) findViewById(R.id.info_ed_age);
        btnSex = (Button) findViewById(R.id.info_btn_sex);
        btnFinish = (Button) findViewById(R.id.info_btn_finish);
        btnSex.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        imgIcon = (CircleImageView) findViewById(R.id.info_img_icon);
        imgIcon.setOnClickListener(this);
        tvTip = (TextView) findViewById(R.id.info_tv_tip);
        mListView = (ListView) findViewById(R.id.info_listview);
        //??????????????????????????????????????????????????????
        unEnableWidget();
        //?????????????????????
        initInfo();
    }
    /**
     * ??????????????????????????????????????????????????????
     */
    private void unEnableWidget() {
        etPhone.setEnabled(false);
        etNickname.setEnabled(false);
        etAge.setEnabled(false);
        btnSex.setEnabled(false);
        btnFinish.setVisibility(View.GONE);
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void enableWidget() {
        //?????????????????????
        etNickname.setEnabled(true);
        etAge.setEnabled(true);
        btnSex.setEnabled(true);
        //???????????????????????????
        btnFinish.setVisibility(View.VISIBLE);
    }
    /**
     * ????????????????????????????????????
     */
    private void initInfo() {
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser == null){
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
        }else{
            initLinkData();
            etPhone.setText(currentUser.getUsername());
            etNickname.setText(currentUser.getNickName());
            etAge.setText(currentUser.getAge() + "");
            etAge.setText(currentUser.getAge() + "");
            btnSex.setText(currentUser.isGender() ? "???" : "???");
            String iconUrl = currentUser.getIconUrl();
            if(!TextUtils.isEmpty(iconUrl)){
                //??????????????????
                Picasso.with(this).load(iconUrl).into(imgIcon);
            }

        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private void initLinkData() {
        childList = new ArrayList<>();
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
                    childList = list;
                    handler.sendEmptyMessage(0);
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_girl_edit:
                //?????????????????????????????????????????????????????????
                enableWidget();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info_btn_sex:
                //???????????????????????????
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("?????????????????????");
                builder.setSingleChoiceItems(
                        new String[]{"???", "???"},
                        0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnSex.setText((which == 0) ?
                                        "???":"???");
                                dialog.dismiss();
                            }
                        });
                builder.show();
                break;
            case R.id.info_btn_finish:
                updateUserInfo();
                break;
            case R.id.info_img_icon:
                openIconChoseDialog();
                break;
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    private void openIconChoseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"??????????????????", "??????"},
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        if(Build.VERSION.SDK_INT >= 23){
                            if(ContextCompat.checkSelfPermission(InfoActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(InfoActivity.this,
                                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }else{
                                openAlbum();
                            }
                        }else{
                            openAlbum();
                        }

                        break;
                    case 1:
                        pictureFile = new File(getExternalCacheDir(),
                                System.currentTimeMillis()+".jpg");
                        try {
                            if(!pictureFile.exists()){
                                pictureFile.createNewFile();
                            }
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT >= 24){
                            imgUri = FileProvider.getUriForFile(InfoActivity.this,
                                    "com.redant.childcare.fileprovider",pictureFile);
                        }else{
                            imgUri = Uri.fromFile(pictureFile);
                        }
                        //????????????
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                        startActivityForResult(intent,0);
                        break;
                }
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * ????????????
     */
    private void openAlbum() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, 1);
    }

    /**
     * ??????????????????
     */
    private void updateUserInfo() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("???????????????...");
        dialog.show();
        //????????????
        currentUser.setNickName(etNickname.getText().toString().trim());
        currentUser.setAge(Integer.parseInt(etAge.getText().toString().trim()));
        currentUser.setGender(btnSex.getText().toString().equals("???") ? true : false);
        currentUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Toast.makeText(InfoActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    //???????????????
                    dialog.dismiss();
                    //?????????????????????????????????
                    unEnableWidget();
                }
                else{
                    Toast.makeText(InfoActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                //????????????
                displayImage(pictureFile.getPath());
                //?????????????????????????????????
                uplaodImg(pictureFile.getPath());
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    //?????????????????????
                    if(Build.VERSION.SDK_INT >= 19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }
    }

    /**
     * 4.4?????????????????????
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imgPath = getImgPath(uri, null);
        displayImage(imgPath);
        uplaodImg(imgPath);
    }

    /**
     * 4.4???????????????????????????
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
//        Log.i(TAG, "handleImageOnKitKat: ???????????????");
        String imgPath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
//                Log.i(TAG, "handleImageOnKitKat: ????????????"+docId);

                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "="+id;
//                Log.i(TAG, "handleImageOnKitKat: selection---------->"+selection);
                imgPath = getImgPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.
                        withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imgPath = getImgPath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imgPath = getImgPath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imgPath = getImgPath(uri, null);
        }
        displayImage(imgPath);
        uplaodImg(imgPath);
    }

    /**
     * ??????????????????????????????????????????
     * @param imgPath
     */
    private void displayImage(String imgPath) {
//        Log.i(TAG, "displayImage: ???????????????"+imgPath);

        if(imgPath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            imgIcon.setImageBitmap(bitmap);
        }
    }

    /**
     * ??????????????????????????????????????????bmob
     * @param imgPath
     */
    private void uplaodImg(String imgPath){
        final BmobFile file = new BmobFile(new File(imgPath));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Log.i(TAG, "done: "+file.getFileUrl());
                    currentUser.setIconUrl(file.getFileUrl());
                    currentUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                Log.i(TAG, "done: ????????????");
                            }else{
                                Log.i(TAG, "done: "+e.getMessage());
                            }
                        }
                    });
                }else{
                    Log.i(TAG, "done: ??????"+e.getMessage());
                }
            }
        });
    }

    /**
     * ???????????????uri??????????????????????????????
     * @param uri
     * @param selection
     * @return
     */
    private String getImgPath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
