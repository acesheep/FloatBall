package com.sheep.zk.floatball.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.manager.SystemBrightManager;
import com.sheep.zk.floatball.service.FloatWindowService;
import com.sheep.zk.floatball.util.CommonUtil;
import com.sheep.zk.floatball.util.ToastTool;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sheep.zk.floatball.R.id.close;


/**
 * Created by sheep on 2017/11/15.
 */

public class FloatWindowBigView extends RelativeLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_brightness_auto)
    TextView tvBrightnessAuto;
    @BindView(R.id.iv_brightness_auto)
    ImageView ivBrightnessAuto;
    @BindView(R.id.sb_brightness)
    SeekBar sbBrightness;
    @BindView(R.id.big_window_layout)
    RelativeLayout bigWindowLayout;
    @BindView(R.id.iv_flashlight)
    ImageView ivFlashlight;
    @BindView(R.id.iv_calculator)
    ImageView ivCalculator;
    @BindView(R.id.iv_alarm)
    ImageView ivAlarm;
    @BindView(R.id.iv_wifi)
    ImageView ivWifi;
    @BindView(R.id.iv_yidsj)
    ImageView ivYidsj;

    private Context context;
    private boolean isAutoBrightness;
    private CameraManager cameraManager;
    private Camera camera;

    public FloatWindowBigView(final Context context) {
        super(context);
        init(context);
        initOperate();



        Button close = (Button) findViewById(R.id.close);
        Button back = (Button) findViewById(R.id.back);

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), FloatWindowService.class);
                context.stopService(intent);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }


    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        ButterKnife.bind(this, view);
        //获取系统当前亮度模式及亮度值
        sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
        isAutoBrightness = SystemBrightManager.isAutoBrightness(context);
        if (isAutoBrightness) {
            ivBrightnessAuto.setSelected(true);
            ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto_choosed));
            tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGreen));
        } else {
            ivBrightnessAuto.setSelected(false);
            ivBrightnessAuto.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_brightness_auto));
            tvBrightnessAuto.setTextColor(ContextCompat.getColor(context, R.color.bgGray));
        }
        //手电筒
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        //wifi
        if(CommonUtil.isWifiConnected(context)){
            ivWifi.setSelected(true);
        }else {
            ivWifi.setSelected(false);
        }
        //移动网络
        if(CommonUtil.isMobileConnected(context)){
            ivYidsj.setSelected(true);
        }else {
            ivYidsj.setSelected(false);
        }
    }

    private void initOperate() {
        sbBrightness.setOnSeekBarChangeListener(this);
        ivBrightnessAuto.setOnClickListener(this);
        ivFlashlight.setOnClickListener(this);
        ivCalculator.setOnClickListener(this);
        ivAlarm.setOnClickListener(this);
        ivWifi.setOnClickListener(this);
        ivYidsj.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_brightness_auto:
                if (ivBrightnessAuto.isSelected()) {
                    ivBrightnessAuto.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_brightness_auto));
                    tvBrightnessAuto.setTextColor(getResources().getColor(R.color.bgGray));
                    ivBrightnessAuto.setSelected(false);
                    SystemBrightManager.stopAutoBrightness(context);
                    sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
                    isAutoBrightness = false;
                } else {
                    ivBrightnessAuto.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_brightness_auto_choosed));
                    tvBrightnessAuto.setTextColor(getResources().getColor(R.color.bgGreen));
                    ivBrightnessAuto.setSelected(true);
                    SystemBrightManager.startAutoBrightness(context);
                    sbBrightness.setProgress(SystemBrightManager.getBrightness(context));
                    isAutoBrightness = true;
                }
                break;
            case R.id.iv_flashlight:
                if (ivFlashlight.isSelected()) {
                    ivFlashlight.setSelected(false);
                    turnOff(cameraManager);
                } else {
                    ivFlashlight.setSelected(true);
                    turnOn(cameraManager);
                }
                break;
            case R.id.iv_calculator:
                back();
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
                back();
                Intent alarms = new Intent(AlarmClock.ACTION_SET_ALARM);
                alarms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(alarms);
                break;
            case R.id.iv_wifi:
                back();
                Intent wifi = new Intent();
                wifi.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                wifi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(wifi);
                break;
            case R.id.iv_yidsj:
                back();
                Intent yidsj = new Intent(android.provider.Settings.ACTION_SETTINGS);
                yidsj.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(yidsj);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SystemBrightManager.setBrightness(context, progress);
        if (isAutoBrightness) {
            SystemBrightManager.stopAutoBrightness(context);
            ivBrightnessAuto.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_brightness_auto));
            tvBrightnessAuto.setTextColor(getResources().getColor(R.color.bgGray));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    private void back() {
        // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
        MyWindowManager.removeBigWindow(context);
        MyWindowManager.createSmallWindow(context);
        //保险关闭手电筒
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode("0", false);
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

}
