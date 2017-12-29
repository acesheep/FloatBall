package com.sheep.zk.floatball.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sheep.zk.floatball.manager.ActivityCollector;

import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    public abstract int setCustomContentViewResourceId();
    public Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setCustomContentViewResourceId());
        context=this;
        ActivityCollector.addActivity(this);
        //使用ButterKnife 注入view
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
