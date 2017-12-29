package com.sheep.zk.floatball.view.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.adapter.RvPowerSavingAdapter;
import com.sheep.zk.floatball.base.BaseActivity;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.bean.ProcessInfo;
import com.sheep.zk.floatball.bean.StorageSize;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.ui.textcounter.CounterView;
import com.sheep.zk.floatball.ui.textcounter.DecimalFormatter;
import com.sheep.zk.floatball.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sheep on 2017/11/28.
 */

public class RvPowerSavingActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_emptty)
    RelativeLayout rlEmptty;
    @BindView(R.id.header)
    RelativeLayout header;
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
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;


    private long mCacheSize = 0;
    private long clearCacheSize = 0;

    //运行的进程列表
    List<ProcessInfo> mProcessInfos = new ArrayList<>();
    RvPowerSavingAdapter rvPowerSavingAdapter;

    @Override
    public int setCustomContentViewResourceId() {
        return R.layout.activity_power_saving_rv;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initViews();
        initOperator();
    }

    private void init() {
        new TaskScan().execute();
    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        rvPowerSavingAdapter = new RvPowerSavingAdapter(R.layout.item_recyclerview_power_saving, mProcessInfos, context);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(rvPowerSavingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerview);

        OnItemDragListener onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            }
        };


        // 开启拖拽
        rvPowerSavingAdapter.enableDragItem(itemTouchHelper, R.id.content, true);
        rvPowerSavingAdapter.setOnItemDragListener(onItemDragListener);


        rvPowerSavingAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        rvPowerSavingAdapter.isFirstOnly(false);
        recyclerview.setAdapter(rvPowerSavingAdapter);
        QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .header(null)
                .minHeaderTranslation(0)
                .footer(btnClean)
                .minFooterTranslation(180)
                .build();
        recyclerview.setOnScrollListener(scrollListener);


    }

    private void initOperator() {
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
                new TaskClean().execute();
                break;
        }
    }


    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.rubbishclean_scan_complete_fade_out);
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


    private class TaskScan extends AsyncTask<Void, Integer, List<ProcessInfo>> {
        private int mProcessesCount = 0;

        @Override
        protected void onPreExecute() {
            progressBarText.setText("扫描中...");
            showProgressBar(true);
        }

        @Override
        protected List<ProcessInfo> doInBackground(Void... params) {
            mCacheSize = 0;

            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            PackageManager pm = getPackageManager();
            List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
            //更新进度
            publishProgress(0, processes.size());
            if (processes != null) {
                for (int i = 0; i < processes.size(); i++) {
                    publishProgress(++mProcessesCount, processes.size());
                    AndroidAppProcess appProcess = processes.get(i);
                    String processName = appProcess.name;
                    ProcessInfo processInfo = new ProcessInfo();
                    processInfo.setPackName(processName);
                    Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{appProcess.pid});
                    Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
                    processInfo.setSize(memoryInfo.getTotalPrivateDirty() * 1024);
                    mCacheSize += processInfo.getSize();
                    try {
                        ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.getPackName(), 0);
                        //8,获取应用的名称
                        processInfo.setName(applicationInfo.loadLabel(pm).toString());
                        //9,获取应用的图标
                        processInfo.setIcon(applicationInfo.loadIcon(pm));
                        //10,判断是否为系统进程
                        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                            processInfo.setSystem(true);
                        } else {
                            processInfo.setSystem(false);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        //需要处理
                        processInfo.name = appProcess.name;
                        processInfo.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                        processInfo.isSystem = true;
                        e.printStackTrace();
                    }
                    processInfoList.add(processInfo);
                }
            }

            return processInfoList;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBarText.setText(getString(R.string.scanning_m_of_n, values[0], values[1]));
        }

        @Override
        protected void onPostExecute(List<ProcessInfo> processInfos) {
            showProgressBar(false);
            if (processInfos.size() <= 0) {
                rlEmptty.setVisibility(View.VISIBLE);
                recyclerview.setVisibility(View.GONE);
            } else {
                rlEmptty.setVisibility(View.GONE);
                recyclerview.setVisibility(View.VISIBLE);
            }

            mProcessInfos.clear();
            mProcessInfos.addAll(processInfos);
            rvPowerSavingAdapter.notifyDataSetChanged();
            if (processInfos.size() > 0) {
                header.setVisibility(View.VISIBLE);
                btnClean.setVisibility(View.VISIBLE);

                StorageSize storageSize = CommonUtil.convertStorageSize(mCacheSize);
                textCounter.setAutoFormat(false);
                textCounter.setFormatter(new DecimalFormatter());
                textCounter.setAutoStart(false);
                textCounter.setStartValue(0f);
                textCounter.setEndValue(storageSize.value);
                textCounter.setIncrement(10f); //每个时间间隔中数字的增量
                textCounter.setTimeInterval(20); //数字变化间的时间间隔
                sufix.setText(storageSize.suffix);
                textCounter.start();
            } else {
                header.setVisibility(View.GONE);
                btnClean.setVisibility(View.GONE);
            }

        }
    }

    //清理进程
    private class TaskClean extends AsyncTask<Void, Void, Long> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBarText.setText("清理中，请稍候！");
            recyclerview.setVisibility(View.GONE);
            clearCacheSize = 0;
        }

        @Override
        protected Long doInBackground(Void... params) {
            ActivityInfo launcherInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                    Intent.CATEGORY_HOME).resolveActivityInfo(getPackageManager(), 0);
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (int i = 0; i < mProcessInfos.size(); i++) {
                ProcessInfo processInfo = mProcessInfos.get(i);
                String processPackageName = processInfo.getPackName();
                if (processPackageName.contains(getPackageName()) || processPackageName.contains(launcherInfo.packageName)
                        || processPackageName.contains("com.jgahaid")
                        || processPackageName.contains("com.wx")) {

                } else {
                    am.killBackgroundProcesses(processPackageName);
                    clearCacheSize += processInfo.getSize();
                }
            }
            return clearCacheSize;
        }

        @Override
        protected void onPostExecute(Long result) {
            showProgressBar(false);
            header.setVisibility(View.GONE);
            btnClean.setVisibility(View.GONE);
            rlEmptty.setVisibility(View.VISIBLE);
            Toast.makeText(context, context.getString(R.string.finishedProcess, Formatter.formatShortFileSize(
                    context, clearCacheSize)), Toast.LENGTH_LONG).show();
            mProcessInfos.clear();
        }
    }

    @Override
    protected void onRestart() {
        BaseApplication.isOnItemClick = false;
        BaseApplication.isBigWindowShow = true;
        MyWindowManager.removeSmallWindow(context);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (BaseApplication.isOnItemClick) {

        } else {
            finish();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        BaseApplication.isBigWindowShow = false;
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
