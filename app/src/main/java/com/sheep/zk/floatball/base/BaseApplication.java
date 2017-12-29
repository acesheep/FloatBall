package com.sheep.zk.floatball.base;

import android.app.Application;
import android.hardware.Camera;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import butterknife.ButterKnife;


public class BaseApplication extends Application {
    public final static boolean IS_DEVELOP_ENVIRONMENT = true;
    public static boolean isInApp=false;
    public static boolean isBigWindowShow=false;
    public static boolean isOnItemClick=false;
    @Override
    public void onCreate() {
        super.onCreate();
        if(IS_DEVELOP_ENVIRONMENT){
            FormatStrategy formatStrategy= PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(true)
                    .methodCount(4)
                    .methodOffset(0)
                    .tag("sheep")
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        }
    }


}
