package com.redant.childcare.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;     
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redant.childcare.R;
import com.redant.childcare.services.StepCount;
import com.redant.childcare.services.StepCounterService;
import com.redant.childcare.services.StepValuePassListener;
import com.redant.childcare.utils.AppConfig;
import com.redant.childcare.utils.ShareUtils;
import com.redant.childcare.utils.UtilTools;
import com.woodnaonly.arcprogress.ArcProgress;

import java.text.DecimalFormat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;


/**
 * Created by jpeng on 16-11-14.
 */
public class FragmentSport extends Fragment {
    private ImageView[] starList = new ImageView[5];
    private ArcProgress arcProgress;//弧形进度条
    private TextView tvWalkDistance, tvCalorieExpend;
    private TextView tvTip;//运动建议
    private TextView tvSteps;//当超过目标时，progressbar会一闪一闪，解决此问题

    private int dialyTarget = 5000;//每日步行目标
    private int dailySteps = 0;//每日步数
    private double walkDistance;//行走距离
    private double calorieExpend;//热量消耗（卡路里）
    private SensorManager sensorManager;
    private StepCount mStepCount;
    private Thread thread;  //定义线程对象
    // 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
    // Handler有两个作用,
    // (1) : 定时执行Message和Runnalbe 对象
    // (2): 让一个动作,在不同的线程中执行.
    private static final String TAG = "FragmentSport";
    Handler handler = new Handler() {
        // Handler对象用于更新当前步数,定时发送消息，调用方法查询数据用于显示
        //主要接受子线程发送的数据, 并用此数据配合主线程更新UI
        //Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据,
        //Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
        //把这些消息放入主线程队列中，配合主线程进行更新UI。

        @Override                  //这个方法是从父类/接口 继承过来的，需要重写一次
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //将初始的步数与新增步数相加
            int steps = dailySteps;
            steps += StepCounterService.steps;
//            Log.i(TAG, FallCountDetector.FALLCOUNTS + "");
            //更新UI
            if(steps > dialyTarget){
                arcProgress.setProgress(dialyTarget);
                tvSteps.setVisibility(View.VISIBLE);
                tvSteps.setText(steps+"");
            }else{
                arcProgress.setProgress(steps);
            }
            tvWalkDistance.setText(formatDouble(calculateDistance(steps)));
            tvCalorieExpend.setText(formatDouble(calculateCalorie(steps)));
            resetStar(changeStepsToStar(steps));
            resetTip(changeStepsToStar(steps));
            //显示通知模块
            Intent intent = new Intent(getContext(), getActivity().getClass());
            PendingIntent pi = PendingIntent.getActivity(getContext(), 0, intent, 0);

            NotificationManager manager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(getContext())
                    .setContentTitle("今日已走步数：")
                    .setContentText(steps + "步")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.childcare_circle)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.childcare))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport, null);
        initView(view);
        initData();

        //启动记步的后台服务
        startStepDetector();
        StepCounterService.steps = 0;
        handler.removeCallbacks(thread);
        if (thread == null) {                                            //线程保持接收状态
            thread = new Thread() {// 子线程用于监听当前步数的变化

                @Override
                public void run() {
                    super.run();
                    while (true) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message msg = new Message();
                        handler.sendMessage(msg);// 通知主线程
                    }
                }
            };
            thread.start();
        }
        return view;
    }

    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addBasePedometerListener();
        } else {
            Intent stepServiceIntent = new Intent(getActivity(), StepCounterService.class);
            getActivity().startService(stepServiceIntent);
        }
    }

    private void addBasePedometerListener() {
        mStepCount = new StepCount();
        mStepCount.setSteps(dailySteps);
        // 获得传感器的类型，这里获得的类型是加速度传感器
        // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean isAvailable = sensorManager.registerListener(mStepCount.getStepDetector(), sensor,
                SensorManager.SENSOR_DELAY_UI);
        mStepCount.initListener(new StepValuePassListener() {
            @Override
            public void stepChanged(int steps) {
                dailySteps = steps;
            }
        });
    }

    private void initData() {
        //从数据库中获取保存的“每日步数”的数据
        dailySteps = ShareUtils.getInt(getActivity(),UtilTools.getdateFormatKey1(),0);
        walkDistance = calculateDistance(dailySteps);
        calorieExpend = calculateCalorie(dailySteps);
        //填充初始数据
        arcProgress.setProgress(dialyTarget);
        tvWalkDistance.setText(formatDouble(walkDistance));
        tvCalorieExpend.setText(formatDouble(calorieExpend));
        //根据步数初始化评分星星
        int starNumber = changeStepsToStar(dailySteps);
        resetStar(starNumber);
    }

    private void resetStar(int starNumber) {
        //重置星星颜色
        for (ImageView star :
                starList) {
            star.setImageResource(R.mipmap.star_normal);
        }
        //根据运动量点亮星星
        for (int i = 0; i < starNumber; i++) {
            starList[i].setImageResource(R.mipmap.star_checked);
        }
    }

    /**
     * 根据运动量做出文字提示
     * @param starNamber
     */
    private void resetTip(int starNamber){
        switch (starNamber){
            case 0:
                tvTip.setText(getString(R.string.sport_tip_0));
                break;
            case 1:
                tvTip.setText(getString(R.string.sport_tip_1));
                break;
            case 2:
                tvTip.setText(getString(R.string.sport_tip_2));
                break;
            case 3:
                tvTip.setText(getString(R.string.sport_tip_3));
                break;
            case 4:
                tvTip.setText(getString(R.string.sport_tip_4));
                break;
            case 5:
                tvTip.setText(getString(R.string.sport_tip_5));
                break;
        }
    }
    private int changeStepsToStar(int dailySteps) {
        int targetUnit = dialyTarget / 5;
        if (dailySteps < targetUnit) {
            return 0;
        } else if (dailySteps < targetUnit * 2) {
            return 1;
        } else if (dailySteps < targetUnit * 3) {
            return 2;
        } else if (dailySteps < targetUnit * 4) {
            return 3;
        } else if (dailySteps < targetUnit * 5) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        //星星评级
        starList[0] = (ImageView) view.findViewById(R.id.img_star_1);
        starList[1] = (ImageView) view.findViewById(R.id.img_star_2);
        starList[2] = (ImageView) view.findViewById(R.id.img_star_3);
        starList[3] = (ImageView) view.findViewById(R.id.img_star_4);
        starList[4] = (ImageView) view.findViewById(R.id.img_star_5);
        //弧形进度条控件
        arcProgress = (ArcProgress) view.findViewById(R.id.arcprogress);
        //获取设置的每日目标
        dialyTarget = ShareUtils.getInt(getActivity(), AppConfig.SHARED_KEY_PROGRESS_MAX, 5000);
        //设置进度条的max值
        arcProgress.setMax(dialyTarget);
        tvWalkDistance = (TextView) view.findViewById(R.id.tv_walk_distance);
        tvCalorieExpend = (TextView) view.findViewById(R.id.tv_calorie_expend);
        tvTip = (TextView) view.findViewById(R.id.f_sport_tv_tip);
        tvSteps = (TextView) view.findViewById(R.id.f_sport_tv_steps);
        tvSteps.setVisibility(View.GONE);
        //自定义字体
        UtilTools.setFontToText(getActivity(), tvWalkDistance);
        UtilTools.setFontToText(getActivity(), tvCalorieExpend);
    }

    /**
     * 根据步数计算步行距离
     *
     * @param steps
     * @return
     */
    public static double calculateDistance(int steps) {
        int step_length = 55;
        double distance;
        if (steps % 2 == 0) {
            distance = (steps / 2) * 3 * step_length * 0.01;
        } else {
            distance = ((steps / 2) * 3 + 1) * step_length * 0.01;
        }
        return distance;
    }

    /**
     * 通过步数计算卡路里的消耗
     *
     * @param steps
     * @return
     */
    public static double calculateCalorie(int steps) {
        int weight = 50;
        double calories;
        calories = weight * calculateDistance(steps) * 0.001;
        return calories;
    }

    /**
     * 格式化double
     *
     * @param doubles
     * @return
     */
    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.#");
        String distanceStr = format.format(doubles);
        return distanceStr.equals("0") ? "0.0"
                : distanceStr;
    }


    @Override
    public void onStop() {
        super.onStop();
        //保存步数数据
        ShareUtils.putInt(getActivity(), UtilTools.getdateFormatKey1(), dailySteps);
    }

}
