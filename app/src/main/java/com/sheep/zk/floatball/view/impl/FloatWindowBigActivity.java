package com.sheep.zk.floatball.view.impl;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.base.BaseActivity;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.manager.SystemBrightManager;
import com.sheep.zk.floatball.ui.SpeedupView;
import com.sheep.zk.floatball.util.CommonUtil;
import com.sheep.zk.floatball.util.ToastTool;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

import static android.os.Build.VERSION_CODES.M;


/**
 * Created by sheep on 2017/11/22.
 */

public class FloatWindowBigActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    @BindView(R.id.sb_brightness)
    SeekBar sbBrightness;
    @BindView(R.id.tv_brightness_auto)
    TextView tvBrightnessAuto;
    @BindView(R.id.iv_brightness_auto)
    ImageView ivBrightnessAuto;
    @BindView(R.id.iv_wifi)
    ImageView ivWifi;
    @BindView(R.id.iv_yidsj)
    ImageView ivYidsj;
    @BindView(R.id.iv_alarm)
    ImageView ivAlarm;
    @BindView(R.id.iv_calculator)
    ImageView ivCalculator;
    @BindView(R.id.iv_flashlight)
    ImageView ivFlashlight;
    @BindView(R.id.tv_percent_bigwindow)
    TextView tvPercentBigwindow;
    @BindView(R.id.rl_ad)
    RelativeLayout rlAd;
    @BindView(R.id.rl_speed_up)
    RelativeLayout rlSpeedUp;
    @BindView(R.id.speedUp_view)
    SpeedupView speedUpView;
    @BindView(R.id.iv_speedup_complete)
    ImageView ivSpeedupComplete;
    @BindView(R.id.iv_wenjj)
    ImageView ivWenjj;
    @BindView(R.id.iv_chaoqsd)
    ImageView ivChaoqsd;
    @BindView(R.id.iv_huancql)
    ImageView ivHuancql;
    @BindView(R.id.iv_lajql)
    ImageView ivLajql;
    @BindView(R.id.iv_jissw)
    ImageView ivJissw;


    private boolean isAutoBrightness;
    private CameraManager cameraManager;
    private Camera camera;
    /**
     * 定时器更新内存数据。
     */
    private Timer timer;
    private Handler handler;

//    private AgentWeb mAdWeb;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speedUpView.setVisibility(View.GONE);
            ivSpeedupComplete.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.speedup_complete_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ivSpeedupComplete.setVisibility(View.GONE);
                    rlSpeedUp.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }


            });
            ivSpeedupComplete.setAnimation(animation);
            animation.start();
        }
    };

    @Override
    public int setCustomContentViewResourceId() {
        return R.layout.activity_floatwindowbig;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e("onCreate");
        initParams();
        initViews();
        initOperators();
    }

    private void initParams() {
        BaseApplication.isInApp = false;
        IntentFilter intentFilter = new IntentFilter("speedup.anim");
        registerReceiver(broadcastReceiver, intentFilter);
        handler = new Handler();
        //获取系统当前亮度模式及亮度值
        sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
        isAutoBrightness = SystemBrightManager.isAutoBrightness(context);
        //手电筒
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private void initViews() {
        if (isAutoBrightness) {
            ivBrightnessAuto.setSelected(true);
            ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto_choosed));
            tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGreen));
        } else {
            ivBrightnessAuto.setSelected(false);
            ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto));
            tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGray));
        }
        //wifi
        if (CommonUtil.isWifiConnected(context)) {
            ivWifi.setSelected(true);
        } else {
            ivWifi.setSelected(false);
        }
        //移动网络
        if (CommonUtil.isMobileConnected(context)) {
            ivYidsj.setSelected(true);
        } else {
            ivYidsj.setSelected(false);
        }
        //顶部webview
//        mAdWeb = AgentWeb.with(this)
//                .setAgentWebParent(rlAd, new RelativeLayout.LayoutParams(-1, -1))
//                .useDefaultIndicator()
//                .defaultProgressBarColor()
//                .createAgentWeb()
//                .ready()
//                .go("http://www.baidu.com");
    }

    private void initOperators() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvPercentBigwindow.setText(MyWindowManager.getUsedPercentValue(context));
                        }
                    });
                }
            }, 0, 1000);
        }
        sbBrightness.setOnSeekBarChangeListener(this);
        ivBrightnessAuto.setOnClickListener(this);
        ivFlashlight.setOnClickListener(this);
        ivCalculator.setOnClickListener(this);
        ivAlarm.setOnClickListener(this);
        ivWifi.setOnClickListener(this);
        ivYidsj.setOnClickListener(this);
        rlSpeedUp.setOnClickListener(this);
        ivLajql.setOnClickListener(this);
        ivHuancql.setOnClickListener(this);
        ivWenjj.setOnClickListener(this);
        ivChaoqsd.setOnClickListener(this);
        ivJissw.setOnClickListener(this);
    }

    //日志观察多少个被杀的程序
    int num = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_brightness_auto:
                if (ivBrightnessAuto.isSelected()) {
                    ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto));
                    tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGray));
                    ivBrightnessAuto.setSelected(false);
                    SystemBrightManager.stopAutoBrightness(context);
                    sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
                    isAutoBrightness = false;
                } else {
                    ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto_choosed));
                    tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGreen));
                    ivBrightnessAuto.setSelected(true);
                    SystemBrightManager.startAutoBrightness(context);
                    sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
                    isAutoBrightness = true;
                }
                break;
            case R.id.iv_flashlight:
                try{
                    if (ivFlashlight.isSelected()) {
                        ivFlashlight.setSelected(false);
                        turnOff(cameraManager);
                    } else {
                        ivFlashlight.setSelected(true);
                        turnOn(cameraManager);
                    }
                }catch (Exception e){
                    ToastTool.getInstance().shortLength(context,"请打开相机权限！",true);
                }

                break;
            case R.id.iv_calculator:
                PackageInfo pak = CommonUtil.getAllApps(context, "Calculator", "calculator");
                if (pak != null) {
                    Intent intent = new Intent();
                    intent = context.getPackageManager().getLaunchIntentForPackage(pak.packageName);
                    context.startActivity(intent);
                } else {
                    ToastTool.getInstance().shortLength(context, "未找到计算器！", true);
                }
                break;
            case R.id.iv_alarm:
                Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
                context.startActivity(alarms);
                break;
            case R.id.iv_wifi:
                Intent wifi = new Intent();
                wifi.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                context.startActivity(wifi);
                break;
            case R.id.iv_yidsj:
                Intent yidsj = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(yidsj);
                break;
            case R.id.rl_speed_up:
                performSpeedupAnim();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
                            if (processes != null) {
                                ActivityInfo launcherInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                                        Intent.CATEGORY_HOME).resolveActivityInfo(getPackageManager(), 0);
                                Logger.e("processed=" + processes.size());
                                for (int i = 0; i < processes.size(); i++) {
                                    AndroidAppProcess appProcess = processes.get(i);
                                    String processName = appProcess.name;
                                    if (processName.contains(getPackageName()) || processName.contains(launcherInfo.packageName)
                                            || processName.contains("com.jgahaid")
                                            || processName.contains("com.wx")
                                            ) {
                                        if (BaseApplication.IS_DEVELOP_ENVIRONMENT) {
                                            Logger.i(processName);
                                        }
                                    } else {
                                        /**清理不可用的内容空间**/
                                        am.killBackgroundProcesses(processName);
                                        if (BaseApplication.IS_DEVELOP_ENVIRONMENT) {
                                            Logger.e((++num) + "===" + processName);
                                        }
                                    }
                                }
                            }
                        } else {
                            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                            List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
                            if (infoList != null) {
                                for (int i = 0; i < infoList.size(); ++i) {
                                    ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                                    //importance 该进程的重要程度  分为几个级别，数值越低就越重要。
                                    Logger.e(appProcessInfo.toString());
                                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                                    if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                                        String[] pkgList = appProcessInfo.pkgList;
                                        for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
                                            am.killBackgroundProcesses(pkgList[j]);
                                        }
                                    }

                                }
                            }
                        }
                    }
                });
                thread.start();
                break;
            case R.id.iv_huancql:
                BaseApplication.isInApp = true;
                Intent intent = new Intent();
                intent.setClass(context, CacheCleanActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_wenjj:
                Intent intentFolder = new Intent(Intent.ACTION_GET_CONTENT);
                intentFolder.setType("*/*");
                intentFolder.addCategory(Intent.CATEGORY_OPENABLE);
                // 添加Category属性
                startActivity(intentFolder);
                break;
            case R.id.iv_chaoqsd:
                BaseApplication.isInApp = true;
                Intent intentCqsd = new Intent();
                intentCqsd.setClass(context, RvPowerSavingActivity.class);
                startActivity(intentCqsd);
                break;
            case R.id.iv_lajql:
                BaseApplication.isInApp = true;
                Intent intentlajql = new Intent();
                intentlajql.setClass(context, RvRubbishCleanActivity.class);
                startActivity(intentlajql);
                break;
            case R.id.iv_jissw:
                try{
                    Intent intentjssw = new Intent(Intent.ACTION_VIEW);
                    intentjssw.setClassName("com.xp.browser","com.xp.browser.activity.SplashActivity");
                    startActivity(intentjssw);
                }catch (Exception e){
                    ToastTool.getInstance().shortLength(context,"当前手机无浏览器！",true);
                }

                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SystemBrightManager.setBrightness(context, progress);
        if (isAutoBrightness) {
            SystemBrightManager.stopAutoBrightness(context);
            ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto));
            tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGray));
            ivBrightnessAuto.setSelected(false);
            isAutoBrightness = false;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        SystemBrightManager.setBrightness(context, seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SystemBrightManager.setBrightness(context, seekBar.getProgress());
    }


    //关闭手电筒
    public void turnOff(CameraManager manager) {
        if (Build.VERSION.SDK_INT >= M) {
            try {
                manager.setTorchMode("0", false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

    //打开手电筒
    public void turnOn(CameraManager manager) {
        if (Build.VERSION.SDK_INT >= M) {
            try {
                manager.setTorchMode("0", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            final PackageManager pm = context.getPackageManager();
            final FeatureInfo[] features = pm.getSystemAvailableFeatures();
            for (final FeatureInfo f : features) {
                if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                    if (null == camera) {
                        camera = Camera.open();
                    }
                    final Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                }
            }
        }
    }

    private void performSpeedupAnim() {
        rlSpeedUp.setVisibility(View.INVISIBLE);
        speedUpView.setVisibility(View.VISIBLE);
        speedUpView.performAnimation(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e("onResume");
    }

    @Override
    protected void onPause() {
        if (BaseApplication.isInApp) {
            BaseApplication.isBigWindowShow = true;
        } else {
            BaseApplication.isBigWindowShow = false;
        }
//        mAdWeb.getWebLifeCycle().onPause();
        overridePendingTransition(0, 0);
        finish();
        //保险关闭手电筒
        turnOff(cameraManager);
        timer.cancel();
        timer = null;
        super.onPause();
        Logger.e("onPause");
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
        Logger.e("onStop");

    }

    @Override
    protected void onDestroy() {
//        mAdWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
        Logger.e("onDestroy");

    }

}
