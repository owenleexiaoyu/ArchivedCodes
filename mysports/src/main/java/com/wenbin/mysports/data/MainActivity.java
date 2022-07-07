package com.wenbin.mysports.data;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wenbin.SettingActivity;
import com.wenbin.mysports.Basketball;
import com.wenbin.mysports.Calorie;
import com.wenbin.mysports.Old;
import com.wenbin.mysports.Pushup;
import com.wenbin.mysports.R;
import com.wenbin.mysports.Run;
import com.wenbin.mysports.Squat;
import com.wenbin.mysports.Walk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by 啸宇 on 2016/10/31.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout run;
    private RelativeLayout squat;
    private RelativeLayout pushup;
    private RelativeLayout walk;
    private RelativeLayout basketball;
    private RelativeLayout history;
    private CircleBar calorieCirclebar;
    private TextView tvsquatcount;
    private TextView tvpushupcount;
    private TextView tvstepcount;
    SharedPreferences calsp = null;
    private TextView tv_tip;
    private static final String TAG = "MainActivity";
    private double calorie = 0;
    private int datecount = 0;
    String sharetext  = null;
    String imgPath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        init();
    }
    public void init(){
        run = (RelativeLayout) findViewById(R.id.runLocationLayout);
        squat = (RelativeLayout) findViewById(R.id.squatLayout);
        pushup = (RelativeLayout) findViewById(R.id.pushupLayout);
        walk = (RelativeLayout) findViewById(R.id.walkLayout);
        basketball = (RelativeLayout) findViewById(R.id.basketballLayout);
        history = (RelativeLayout) findViewById(R.id.historyLayout);
        calorieCirclebar = (CircleBar) findViewById(R.id.calorieCircleBar);
        run.setOnClickListener(this);
        squat.setOnClickListener(this);
        pushup.setOnClickListener(this);
        walk.setOnClickListener(this);
        basketball.setOnClickListener(this);
        history.setOnClickListener(this);
        calorieCirclebar.setOnClickListener(this);
        tvsquatcount = (TextView) findViewById(R.id.squatcount);
        tvpushupcount = (TextView) findViewById(R.id.pushupcount);
        tvstepcount = (TextView) findViewById(R.id.stepcount);
        tvsquatcount.setText("0个");
        tvpushupcount.setText("0个");
        tvstepcount.setText("0步");
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        getFileFromAsset("sharepic.jpg");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.runLocationLayout:
                intent.setClass(MainActivity.this,Run.class);
                break;
            case R.id.squatLayout:
                intent.setClass(MainActivity.this,Squat.class);
                break;
            case R.id.pushupLayout:
                intent.setClass(MainActivity.this,Pushup.class);
                break;
            case R.id.walkLayout:
                intent.setClass(MainActivity.this,Walk.class);
                break;
            case R.id.basketballLayout:
                intent.setClass(MainActivity.this,Basketball.class);
                break;
            case R.id.historyLayout:
                intent.setClass(MainActivity.this,Old.class);
                break;
            case R.id.calorieCircleBar:
                intent.setClass(MainActivity.this,Calorie.class);
                break;
        }
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }if (item.getItemId() == R.id.action_share) {
//            Toast.makeText(MainActivity.this,"分享",Toast.LENGTH_SHORT).show();
            shareUtil(sharetext,imgPath);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        calsp = getSharedPreferences(SettingActivity.CALORIE_GOAL,Context.MODE_PRIVATE);
        calorieCirclebar.setMaxNumber(Float.valueOf(calsp.getString(SettingActivity.KEY_CALORIE_GOAL,"3000")));
//        Toast.makeText(this,"最大卡路里："+calsp.getString(SettingActivity.KEY_CALORIE_GOAL,"3000"),Toast.LENGTH_SHORT).show();
        DataUtils utils = DataUtils.getInstance(getApplicationContext());
        utils.updateNewDayData();

        double run = utils.get(DataUtils.UTILS_SPORT_RUN);
        double squat = utils.get(DataUtils.UTILS_SPORT_SQUAT);
        double pushup = utils.get(DataUtils.UTILS_SPORT_PUSHUP);
        double walk = utils.get(DataUtils.UTILS_SPORT_WALK);

        datecount = utils.getDateCount();
        double basketball = utils.get(DataUtils.UTILS_SPORT_BASKETBALL);
        calorie = run+squat+pushup+walk+basketball;
        sharetext = "这是我使用【趣运动】锻炼的第"+datecount+"天，今天我消耗了"+calorie+"卡路里，明天继续~~";
        imgPath = Environment.getExternalStorageDirectory().toString() + "/mysports"+"/sharepic.jpg";
        calorieCirclebar.update((int) calorie,3000);
        if(calorie/Float.valueOf(calsp.getString(SettingActivity.KEY_CALORIE_GOAL,"3000"))>0.8){
            tv_tip.setText("今天不错哦，加油继续努力！");
        }else{
            tv_tip.setText("今天的活动量较少，要加油哦！");
        }
        tvpushupcount.setText(utils.getData(DataUtils.UTILS_SPORT_PUSHUP)+"个");
        tvsquatcount.setText(utils.getData(DataUtils.UTILS_SPORT_SQUAT)+"个");
        tvstepcount.setText(utils.getData(DataUtils.UTILS_SPORT_WALK)+"步");
    }

    public void shareUtil( String msgText,
                          String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT,"趣运动");
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享到"));
    }
    /*
     * 获取Asset内的文件夹
     * @param fileName 必须是完整文件名（文件名+格式）
     */
    private void getFileFromAsset(String fileName) {
        InputStream fileStream = null;
        try {
            //获取指定Assets文件流
            fileStream = getResources().getAssets().open(fileName);
            //转化为bitmap对象
            Bitmap bitmap = BitmapFactory.decodeStream(fileStream);
            saveInSdCard(fileName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 保存到sb卡内
     * @param fileName 必须是完整文件名（文件名+格式）
     * @param bitmap
     */
    private void saveInSdCard(String filename, Bitmap bitmap) throws IOException {
        //检查是否存在sd卡
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请插入sd卡", Toast.LENGTH_LONG).show();
            return;
        }

        /*
         * 在Android中1.5、1.6的sdcard目录为/sdcard，而Android2.0以上都是/mnt/sdcard，因此如果我们在保存时直接写具体目录会不妥，因此我们可以使用:
         * Environment.getExternalStorageDirectory();获取sdcard目录；
         */
        String directory = Environment.getExternalStorageDirectory().toString() + "/mysports";
        File rootFile = new File(directory);
        //如不存在文件夹，则新建文件夹
        if (!rootFile.exists())
            rootFile.mkdir();
        //在文件夹下加入获取的文件
        File file = new File(directory, filename);
        try {
            //文件输出流
            FileOutputStream out = new FileOutputStream(file);
            //bitmp压缩到本地，原图就100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
