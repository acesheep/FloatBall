package com.sheep.zk.floatball.view.impl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.adapter.LvPowerSavingAdapter;
import com.sheep.zk.floatball.base.BaseActivity;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.bean.ProcessInfo;
import com.sheep.zk.floatball.bean.StorageSize;
import com.sheep.zk.floatball.ui.textcounter.CounterView;
import com.sheep.zk.floatball.ui.textcounter.DecimalFormatter;
import com.sheep.zk.floatball.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sheep on 2017/11/28.
 */

public class LvPowerSavingActivity extends BaseActivity implements View.OnClickListener{


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



    private long mCacheSize = 0;



    //运行的进程列表
    List<ProcessInfo> mProcessInfos=new ArrayList<>();
    LvPowerSavingAdapter lvPowerSavingAdapter;

    @Override
    public int setCustomContentViewResourceId() {
        return R.layout.activity_power_saving;
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
        listview.setEmptyView(rlEmptty);
        lvPowerSavingAdapter=new LvPowerSavingAdapter(context,mProcessInfos);
        listview.setAdapter(lvPowerSavingAdapter);
        listview.setOnItemClickListener(lvPowerSavingAdapter);
        QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .header(null)
                .minHeaderTranslation(0)
                .footer(btnClean)
                .minFooterTranslation(180)
                .build();
        listview.setOnScrollListener(scrollListener);


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
                break;
        }
    }







//    @Override
//    public void onCleanCompleted(Context context, long cacheSize) {
//        Toast.makeText(context, context.getString(R.string.cleaned, Formatter.formatShortFileSize(
//                context, cacheSize)), Toast.LENGTH_LONG).show();
//        header.setVisibility(View.GONE);
//        btnClean.setVisibility(View.GONE);
//        mCacheListItem.clear();
//        rvCacheCleanAdapter.notifyDataSetChanged();
//
//    }
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
    }

    @Override
    protected void onPause() {
        finish();
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

    private class TaskScan extends AsyncTask<Void,Integer,List<ProcessInfo>>{
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
            PackageManager pm =getPackageManager();
            List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
            //更新进度
            publishProgress(0,processes.size());
            if (processes != null) {
                for (int i = 0; i < processes.size(); i++) {
                    publishProgress(++mProcessesCount, processes.size());
                    AndroidAppProcess appProcess = processes.get(i);
                    String processName = appProcess.name;
                    ProcessInfo processInfo = new ProcessInfo();
                    processInfo.setPackName(processName);
                    android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{appProcess.pid});
                    android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
                    processInfo.setSize(memoryInfo.getTotalPrivateDirty()*1024);
                    mCacheSize+=processInfo.getSize();
                    try {
                        ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.getPackName(), 0);
                        //8,获取应用的名称
                        processInfo.setName(applicationInfo.loadLabel(pm).toString());
                        //9,获取应用的图标
                        processInfo.setIcon(applicationInfo.loadIcon(pm));
                        //10,判断是否为系统进程
                        if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                            processInfo.setSystem(true);
                        }else{
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
            if(processInfos.size()<=0){
                rlEmptty.setVisibility(View.VISIBLE);
            }else {
                rlEmptty.setVisibility(View.GONE);
            }

            mProcessInfos.clear();
            mProcessInfos.addAll(processInfos);
            lvPowerSavingAdapter.notifyDataSetChanged();
            header.setVisibility(View.GONE);
            if(processInfos.size()>0){
                header.setVisibility(View.VISIBLE);
                btnClean.setVisibility(View.VISIBLE);

                StorageSize storageSize= CommonUtil.convertStorageSize(mCacheSize);
                textCounter.setAutoFormat(false);
                textCounter.setFormatter(new DecimalFormatter());
                textCounter.setAutoStart(false);
                textCounter.setStartValue(0f);
                textCounter.setEndValue(storageSize.value);
                textCounter.setIncrement(10f); //每个时间间隔中数字的增量
                textCounter.setTimeInterval(50); //数字变化间的时间间隔
                sufix.setText(storageSize.suffix);
                textCounter.start();
            }else {
                header.setVisibility(View.GONE);
                btnClean.setVisibility(View.GONE);
            }

        }
    }

}
