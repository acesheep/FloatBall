package com.sheep.zk.floatball.util;

import android.content.Context;
import android.widget.Toast;

import com.sheep.zk.floatball.base.BaseApplication;


public class ToastTool {

    private static ToastTool instance;

    public synchronized static ToastTool getInstance() {
        if (instance == null) {
            instance = new ToastTool();
        }
        return instance;
    }


    public void shortLength(Context context, String msg, boolean isProductEnvironmentShow) {
        if (isProductEnvironmentShow) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            if (BaseApplication.IS_DEVELOP_ENVIRONMENT) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void longLength(Context context, String msg, boolean isProductEnvironmentShow) {
        if (isProductEnvironmentShow) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } else {
            if (BaseApplication.IS_DEVELOP_ENVIRONMENT) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

}
