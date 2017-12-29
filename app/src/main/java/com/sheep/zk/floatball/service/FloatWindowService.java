package com.sheep.zk.floatball.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;

import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.manager.MyWindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sheep on 2017/11/15.
 */

public class FloatWindowService extends Service {
    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗,更新内存数据。
     */
    private Timer timer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开启定时器，每隔1秒刷新一次
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行
        timer.cancel();
        timer = null;
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {
            //没有悬浮窗显示，则创建悬浮窗。
            if (!MyWindowManager.isWindowShowing()&& !BaseApplication.isBigWindowShow) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.createSmallWindow(getApplicationContext());
                    }
                });
            }
            //有悬浮窗显示，则更新内存数据。
            else if (MyWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyWindowManager.updateUsedPercent(getApplicationContext());
                    }
                });
            }

        }

    }
}
