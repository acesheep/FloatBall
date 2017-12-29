package com.sheep.zk.floatball.view.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.service.FloatWindowService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);

        Intent intent1=new Intent(this, FloatWindowBigActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
        BaseApplication.isBigWindowShow=true;
        MyWindowManager.removeSmallWindow(this);
        finish();
    }
}
