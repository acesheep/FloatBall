package com.sheep.zk.floatball.view.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.KeyEvent;
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
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.adapter.RvRubbishCleanAdapter;
import com.sheep.zk.floatball.base.BaseActivity;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.bean.RvRubbishLevel1;
import com.sheep.zk.floatball.bean.RvRubbishLevel2;
import com.sheep.zk.floatball.bean.RvRubbishPerson;
import com.sheep.zk.floatball.manager.MyWindowManager;
import com.sheep.zk.floatball.service.RvRubbishCleanerService;
import com.sheep.zk.floatball.util.Constant;
import com.sheep.zk.floatball.util.ToastTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

import static com.sheep.zk.floatball.R.id.recyclerview;

/**
 * Created by sheep on 2017/11/28.
 */

public class RvRubbishCleanActivity extends BaseActivity implements View.OnClickListener, RvRubbishCleanerService.OnActionListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_emptty)
    RelativeLayout rlEmptty;
    @BindView(R.id.header)
    RelativeLayout header;
    @BindView(R.id.btn_clean)
    Button btnClean;
    @BindView(R.id.progressBarText)
    TextView progressBarText;
    @BindView(R.id.progressBar)
    LinearLayout progressBar;
    @BindView(R.id.rv_rubbish)
    RecyclerView rvRubbish;
    @BindView(R.id.tv_size)
    TextView tvSize;

    private RvRubbishCleanerService mRvRubbishCleanerService;
    private boolean mAlreadyScanned = false;
    //垃圾文件列表
    private List[] lists;
    private long scanRubbishSize;
    private long sysSize;
    private long temSize;
    private long logSize;
    private long bacSize;
    private long apkSize;
    public static ArrayList<File> filesBackup;
    public static ArrayList<File> filesLog;
    public static ArrayList<File> filesApk;
    public static ArrayList<File> filesTemp;
    private RvRubbishCleanAdapter rvRubbishCleanAdapter;
    private ArrayList<MultiItemEntity> res = new ArrayList<>();
    //rv多级菜单的person级
    public static RvRubbishPerson rvRubbishPersonSys;
    public static RvRubbishPerson rvRubbishPersonBac;
    public static RvRubbishPerson rvRubbishPersonApk;
    //rv多级菜单的level1级（此就为系统垃圾的临时文件和日志文件）
    public static RvRubbishLevel1 rvRubbishLevel1Temp;
    public static RvRubbishLevel1 rvRubbishLevel1Log;
    public static ArrayList<RvRubbishLevel1> listBac = new ArrayList<>();
    public static ArrayList<RvRubbishLevel1> listApk = new ArrayList<>();
    //rv多级菜单的level2级（）
    public static ArrayList<RvRubbishLevel2> listTemp = new ArrayList<>();
    public static ArrayList<RvRubbishLevel2> listLog = new ArrayList<>();
    //需要删除的文件
    public static ArrayList<File> filesDelete = new ArrayList<>();
    //删除了多少个文件
    private int filesDeleteCount;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showProgressBar(false);
            rvRubbishCleanAdapter.notifyDataSetChanged();
            rlEmptty.setVisibility(View.GONE);
            btnClean.setVisibility(View.VISIBLE);
            rvRubbish.setVisibility(View.VISIBLE);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRvRubbishCleanerService = ((RvRubbishCleanerService.RvRubbishCleanerServiceBinder) service).getService();
            mRvRubbishCleanerService.setOnActionListener(RvRubbishCleanActivity.this);


            if (!mRvRubbishCleanerService.isScanning() && !mAlreadyScanned) {
                mRvRubbishCleanerService.scanCache();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRvRubbishCleanerService.setOnActionListener(null);
            mRvRubbishCleanerService = null;
        }
    };

    @Override
    public int setCustomContentViewResourceId() {
        return R.layout.activity_rubbish_clean_rv;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initOperator();
    }

    private void initViews() {
        tvSize.setText(Formatter.formatShortFileSize(context, 0));

        rvRubbishCleanAdapter = new RvRubbishCleanAdapter(res, context);
        rvRubbish.setAdapter(rvRubbishCleanAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //setLayoutManager should be called after setAdapter
        rvRubbish.setLayoutManager(linearLayoutManager);
        QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .header(null)
                .minHeaderTranslation(0)
                .footer(btnClean)
                .minFooterTranslation(180)
                .build();
        rvRubbish.setOnScrollListener(scrollListener);
    }

    private void initOperator() {
        bindService(new Intent(RvRubbishCleanActivity.this, RvRubbishCleanerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
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

                if (mRvRubbishCleanerService != null && !mRvRubbishCleanerService.isScanning() &&
                        !mRvRubbishCleanerService.isCleaning() && filesDelete.size() > 0) {
                    mRvRubbishCleanerService.cleanRubbish();
                }
                break;
        }
    }

    @Override
    public void onScanStarted(Context context) {
        showProgressBar(true);
        progressBarText.setText("扫描中，请稍后...");

    }

    @Override
    public void onScanProgressUpdated(Context context, long size) {
        tvSize.setText(Formatter.formatShortFileSize(context, size));
    }


    @Override
    public void onScanCompleted(final Context context, final List[] files, long rubbishSize, long sysSize, final long temSize, final long logSize, long bacSize, long apkSize) {
        scanRubbishSize = rubbishSize;
        this.logSize = logSize;
        this.temSize = temSize;
        this.sysSize = sysSize;
        this.bacSize = bacSize;
        this.apkSize = apkSize;
        tvSize.setText(Formatter.formatShortFileSize(context, scanRubbishSize));
        if (rubbishSize <= 0) {
            showProgressBar(false);
            rlEmptty.setVisibility(View.VISIBLE);
        } else {
            lists = files;
            filesBackup = (ArrayList<File>) lists[0];
            filesApk = (ArrayList<File>) lists[1];
            filesLog = (ArrayList<File>) lists[2];
            filesTemp = (ArrayList<File>) lists[3];
            filesDelete.clear();
            listApk.clear();
            listBac.clear();
            listLog.clear();
            listTemp.clear();
            for (File file : filesApk) {
                filesDelete.add(file);
            }
            for (File file : filesBackup) {
                filesDelete.add(file);
            }
            for (File file : filesTemp) {
                filesDelete.add(file);
            }
            for (File file : filesLog) {
                filesDelete.add(file);
            }
            Logger.e("filesDelete=" + filesDelete.size());

            rvRubbishPersonSys = new RvRubbishPerson("系统垃圾", Formatter.formatShortFileSize(context, sysSize), sysSize, true, Constant.ISXITONGLAJI);
            rvRubbishPersonBac = new RvRubbishPerson("空余备份", Formatter.formatShortFileSize(context, bacSize), bacSize, true, Constant.ISKONGYUBEIFEN);
            rvRubbishPersonApk = new RvRubbishPerson("安装包", Formatter.formatShortFileSize(context, apkSize), apkSize, true, Constant.ISANZHUANGBAO);
            Thread wordThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //apk
                    PackageManager packageManager = getPackageManager();
                    for (int i = 0; i < filesApk.size(); i++) {
                        File file = filesApk.get(i);
                        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filesApk.get(i).getAbsolutePath(), PackageManager.GET_ACTIVITIES);
                        if (packageInfo != null) {
                            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                            applicationInfo.sourceDir = filesApk.get(i).getAbsolutePath();
                            applicationInfo.publicSourceDir = filesApk.get(i).getAbsolutePath();
                            RvRubbishLevel1 rvRubbishLevel1 = new RvRubbishLevel1(applicationInfo.loadLabel(packageManager).toString(),
                                    Formatter.formatShortFileSize(context, filesApk.get(i).length()),
                                    applicationInfo.loadIcon(packageManager), filesApk.get(i).length(), Constant.ISAPK, isApkInstalled(applicationInfo.packageName),
                                    packageInfo.versionName == null ? "0" : packageInfo.versionName, true, file);
                            if (filesApk.get(i).length() > 0) {
                                rvRubbishPersonApk.addSubItem(rvRubbishLevel1);
                                listApk.add(rvRubbishLevel1);
                            }
                        }
                    }
                    //bac
                    for (int i = 0; i < filesBackup.size(); i++) {
                        File file = filesBackup.get(i);
                        RvRubbishLevel1 rvRubbishLevel1 = new RvRubbishLevel1(filesBackup.get(i).getName(),
                                Formatter.formatShortFileSize(context, filesBackup.get(i).length()),
                                ContextCompat.getDrawable(context, R.mipmap.ic_orange_item), filesBackup.get(i).length(), Constant.ISBAC, true, file);
                        if (rvRubbishLevel1.bytes > 0) {
                            rvRubbishPersonBac.addSubItem(rvRubbishLevel1);
                            listBac.add(rvRubbishLevel1);
                        }
                    }
                    //sys
                    rvRubbishLevel1Temp = new RvRubbishLevel1("临时文件", Formatter.formatShortFileSize(context, temSize),
                            ContextCompat.getDrawable(context, R.mipmap.icon_folder), temSize, Constant.ISSYSTEMP, true);
                    rvRubbishLevel1Log = new RvRubbishLevel1("日志文件", Formatter.formatShortFileSize(context, logSize),
                            ContextCompat.getDrawable(context, R.mipmap.icon_folder), logSize, Constant.ISSYSLOG, true);

                    for (int i = 0; i < filesTemp.size(); i++) {
                        File file = filesTemp.get(i);
                        RvRubbishLevel2 rvRubbishLevel2 = new RvRubbishLevel2(filesTemp.get(i).getName(),
                                Formatter.formatShortFileSize(context, filesTemp.get(i).length()),
                                ContextCompat.getDrawable(context, R.mipmap.ic_orange_item), filesTemp.get(i).length(), true, file, Constant.ISTEMP);
                        if (rvRubbishLevel2.bytes > 0) {
                            rvRubbishLevel1Temp.addSubItem(rvRubbishLevel2);
                            listTemp.add(rvRubbishLevel2);
                        }
                    }

                    for (int i = 0; i < filesLog.size(); i++) {
                        File file = filesLog.get(i);
                        RvRubbishLevel2 rvRubbishLevel2 = new RvRubbishLevel2(filesLog.get(i).getName(),
                                Formatter.formatShortFileSize(context, filesLog.get(i).length()),
                                ContextCompat.getDrawable(context, R.mipmap.ic_orange_item), filesLog.get(i).length(), true, file, Constant.ISLOG);
                        if (rvRubbishLevel2.bytes > 0) {
                            rvRubbishLevel1Log.addSubItem(rvRubbishLevel2);
                            listLog.add(rvRubbishLevel2);
                        }
                    }
                    rvRubbishPersonSys.addSubItem(rvRubbishLevel1Temp);
                    rvRubbishPersonSys.addSubItem(rvRubbishLevel1Log);

                    res.add(rvRubbishPersonSys);
                    res.add(rvRubbishPersonBac);
                    res.add(rvRubbishPersonApk);
                    handler.sendEmptyMessage(RESULT_OK);
                }
            });
            wordThread.start();

        }


        if (!mAlreadyScanned) {
            mAlreadyScanned = true;
        }
    }


    @Override
    public void onCleanStarted(Context context) {
        filesDeleteCount = filesDelete.size();
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setText("清理中，请稍候！");
        rvRubbish.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
        btnClean.setVisibility(View.GONE);
    }

    @Override
    public void onCleanCompleted(Context context, long rubbishSize) {
        progressBar.setVisibility(View.GONE);
        ToastTool.getInstance().shortLength(context, "清理掉了" +
                filesDelete.size() + "个文件，共释放了" + Formatter.formatShortFileSize(context, rubbishSize) + "的空间！", true);
        rlEmptty.setVisibility(View.VISIBLE);
        Logger.e("filesDelete=" + filesDelete.size());
        Logger.e("filesLog=" + filesLog.size());
        Logger.e("filesTemp=" + filesTemp.size());
        Logger.e("filesBac=" + filesBackup.size());
        Logger.e("filesApk=" + filesApk.size());
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

    private boolean isApkInstalled(String packagename) {
        PackageManager localPackageManager = getPackageManager();
        try {
            PackageInfo localPackageInfo = localPackageManager.getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean flag = rvRubbishCleanAdapter.exit();
            if (flag) {
                return super.onKeyDown(keyCode, event);

            } else {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseApplication.isOnItemClick = false;
        BaseApplication.isBigWindowShow = true;
        MyWindowManager.removeSmallWindow(context);
    }

    @Override
    protected void onPause() {
        if (BaseApplication.isOnItemClick) {

        } else {
            unbindService(mServiceConnection);
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
