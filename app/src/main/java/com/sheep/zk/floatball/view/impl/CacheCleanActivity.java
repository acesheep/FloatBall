package com.sheep.zk.floatball.view.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.adapter.RvCacheCleanAdapter;
import com.sheep.zk.floatball.base.BaseActivity;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.bean.CacheListItem;
import com.sheep.zk.floatball.bean.StorageSize;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.service.CacheCleanerService;
import com.sheep.zk.floatball.ui.textcounter.CounterView;
import com.sheep.zk.floatball.ui.textcounter.DecimalFormatter;
import com.sheep.zk.floatball.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sheep on 2017/11/28.
 */

public class CacheCleanActivity extends BaseActivity implements View.OnClickListener, CacheCleanerService.OnActionListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_emptty)
    RelativeLayout rlEmptty;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.btn_clean)
    Button btnClean;
    @BindView(R.id.textCounter)
    CounterView textCounter;
    @BindView(R.id.sufix)
    TextView sufix;
    @BindView(R.id.progressBarText)
    TextView progressBarText;
    @BindView(R.id.progressBar)
    LinearLayout progressBar;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private CacheCleanerService mCacheCleanerService;
    private boolean mAlreadyScanned = false;
    //app缓存情况列表
    List<CacheListItem> mCacheListItem = new ArrayList<>();
    RvCacheCleanAdapter rvCacheCleanAdapter;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCacheCleanerService = ((CacheCleanerService.CleanerServiceBinder) service).getService();
            mCacheCleanerService.setOnActionListener(CacheCleanActivity.this);


            if (!mCacheCleanerService.isScanning() && !mAlreadyScanned) {
                mCacheCleanerService.scanCache();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCacheCleanerService.setOnActionListener(null);
            mCacheCleanerService = null;
        }
    };

    @Override
    public int setCustomContentViewResourceId() {
        return R.layout.activity_cache_clean;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initOperator();
    }

    private void initViews() {
        listview.setEmptyView(tvEmpty);
        rvCacheCleanAdapter = new RvCacheCleanAdapter(context, mCacheListItem);
        listview.setAdapter(rvCacheCleanAdapter);
        listview.setOnItemClickListener(rvCacheCleanAdapter);
        QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .header(null)
                .minHeaderTranslation(0)
                .footer(btnClean)
                .minFooterTranslation(180)
                .build();
        listview.setOnScrollListener(scrollListener);


    }

    private void initOperator() {
        bindService(new Intent(CacheCleanActivity.this, CacheCleanerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        ivBack.setOnClickListener(this);
        btnClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_clean:
                if (mCacheCleanerService != null && !mCacheCleanerService.isScanning() &&
                        !mCacheCleanerService.isCleaning() && mCacheCleanerService.getCacheSize() > 0) {

                    mCacheCleanerService.cleanCache();
                }
                break;
        }
    }

    @Override
    public void onScanStarted(Context context) {
        progressBarText.setText("扫描中...");
        showProgressBar(true);

    }



    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        progressBarText.setText(getString(R.string.scanning_m_of_n, current, max));

    }

    @Override
    public void onScanCompleted(Context context, List<CacheListItem> apps) {
        showProgressBar(false);
        if(apps.size()<=0){
            rlEmptty.setVisibility(View.VISIBLE);
        }else {
            rlEmptty.setVisibility(View.GONE);
        }
        mCacheListItem.clear();
        mCacheListItem.addAll(apps);
        rvCacheCleanAdapter.notifyDataSetChanged();
        header.setVisibility(View.GONE);
        if(apps.size()>0){
            header.setVisibility(View.VISIBLE);
            btnClean.setVisibility(View.VISIBLE);

            long medMemory = mCacheCleanerService != null ? mCacheCleanerService.getCacheSize() : 0;
            StorageSize storageSize= CommonUtil.convertStorageSize(medMemory);
            textCounter.setAutoFormat(false);
            textCounter.setFormatter(new DecimalFormatter());
            textCounter.setAutoStart(false);
            textCounter.setStartValue(0f);
            textCounter.setEndValue(storageSize.value);
            textCounter.setIncrement(5f); //每个时间间隔中数字的增量
            textCounter.setTimeInterval(50); //数字变化间的时间间隔
            sufix.setText(storageSize.suffix);
            textCounter.start();
        }else {
            header.setVisibility(View.GONE);
            btnClean.setVisibility(View.GONE);
        }
        if (!mAlreadyScanned) {
            mAlreadyScanned = true;

        }
    }

    @Override
    public void onCleanStarted(Context context) {
        if (isProgressBarVisible()) {
            showProgressBar(false);
        }


    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {
        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(
                context, cacheSize)), Toast.LENGTH_LONG).show();
        header.setVisibility(View.GONE);
        btnClean.setVisibility(View.GONE);
        mCacheListItem.clear();
        rvCacheCleanAdapter.notifyDataSetChanged();

    }
    private boolean isProgressBarVisible() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    private void showProgressBar(boolean show) {
        if(show){
            progressBar.setVisibility(View.VISIBLE);
        }else {
            Animation animation=AnimationUtils.loadAnimation(context,R.anim.rubbishclean_scan_complete_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            progressBar.setAnimation(animation);
            animation.start();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        BaseApplication.isOnItemClick=false;
        BaseApplication.isBigWindowShow=true;
        MyWindowManager.removeSmallWindow(context);
    }

    @Override
    protected void onPause() {
        if(BaseApplication.isOnItemClick){

        }else {
            unbindService(mServiceConnection);
            finish();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        BaseApplication.isBigWindowShow=false;
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
