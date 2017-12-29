package com.sheep.zk.floatball.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.support.annotation.IntDef;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.bean.CacheListItem;
import com.sheep.zk.floatball.util.CleanUtils;
import com.sheep.zk.floatball.util.SDCardUtils;
import com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sheep on 2017/11/28.
 */
public class RvRubbishCleanerService extends Service {

    private Method mGetPackageSizeInfoMethod, mFreeStorageAndNotifyMethod;
    private OnActionListener mOnActionListener;
    private boolean mIsScanning = false;
    private boolean mIsCleaning = false;
    private long mRubbishSize = 0;
    private long mLogSize=0;
    private long mBakSize=0;
    private long mApkSize=0;
    private long mTemSize=0;
    private long mSysSize=0;
    private Timer timer;
    ArrayList<File> filesBackup= new ArrayList<>();
    ArrayList<File> filesApk= new ArrayList<>();
    ArrayList<File> filesLog= new ArrayList<>();
    ArrayList<File> filesTemp=new ArrayList<>();
    //删掉的文件数目
    private long deletedSize;
    //activity中回调
    public static interface OnActionListener {
        public void onScanStarted(Context context);

        public void onScanProgressUpdated(Context context, long size);

        public void onScanCompleted(Context context, List[] files,long size,long sysSize,long temSize,long logSize,long bacSize,long apkSize);

        public void onCleanStarted(Context context);

        public void onCleanCompleted(Context context, long cacheSize);
    }


    @Override
    public void onCreate() {
        //反射获取到两种方法
        try {
            //获取app所有缓存
            mGetPackageSizeInfoMethod = getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            mFreeStorageAndNotifyMethod = getPackageManager().getClass().getMethod(
                    "freeStorageAndNotify", long.class, IPackageDataObserver.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class RvRubbishCleanerServiceBinder extends Binder {

        public RvRubbishCleanerService getService() {
            return RvRubbishCleanerService.this;
        }

    }

    private RvRubbishCleanerServiceBinder mBinder = new RvRubbishCleanerServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    //查询垃圾
    private class TaskScan extends AsyncTask<Void, Long, Void> {


        @Override
        protected void onPreExecute() {
            mRubbishSize = 0;
            mApkSize=0;
            mBakSize=0;
            mSysSize=0;
            mLogSize=0;
            mTemSize=0;
            if (mOnActionListener != null) {
                mOnActionListener.onScanStarted(RvRubbishCleanerService.this);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(timer==null){
                timer=new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        publishProgress(mRubbishSize);
                    }
                },0,100);
            }
            SDCardUtils sdCardUtils=new SDCardUtils();
            String SDCARD_ROOT = sdCardUtils.getSdPath();
            getallFiles(SDCARD_ROOT);

            return null;
        }


        @Override
        protected void onProgressUpdate(Long... values) {
            if (mOnActionListener != null) {
                mOnActionListener.onScanProgressUpdated(RvRubbishCleanerService.this, values[0]);
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            List[] lists=new List[]{filesBackup,filesApk,filesLog,filesTemp};
            if(timer!=null){
                timer.cancel();
                timer=null;
            }
            if (mOnActionListener != null) {
                mOnActionListener.onScanCompleted(RvRubbishCleanerService.this, lists,mRubbishSize,mSysSize,mTemSize,mLogSize,mBakSize,mApkSize);
            }

            mIsScanning = false;
        }


    }


    //清除垃圾
    private class TaskClean extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (mOnActionListener != null) {
                mOnActionListener.onCleanStarted(RvRubbishCleanerService.this);
            }
            deletedSize=0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=0;i<RvRubbishCleanActivity.filesDelete.size();i++){
                File file=RvRubbishCleanActivity.filesDelete.get(i);
                deletedSize+=file.length();
                file.delete();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            if (mOnActionListener != null) {
                mOnActionListener.onCleanCompleted(RvRubbishCleanerService.this, deletedSize);
            }

            mIsCleaning = false;
        }
    }


    public void scanCache() {
        mIsScanning = true;
        new TaskScan().execute();
    }

    public void cleanRubbish() {
        mIsCleaning = true;

        new TaskClean().execute();
    }

    public void setOnActionListener(OnActionListener listener) {
        mOnActionListener = listener;
    }

    public boolean isScanning() {
        return mIsScanning;
    }

    public boolean isCleaning() {
        return mIsCleaning;
    }



    //遍历查询文件
    public void getallFiles(String sd) {

        try {//遍历可能遇到.开头的文件
            File file = new File(sd);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        getallFiles(files[i].getAbsolutePath());// 递归查找
                    } else {
                            if (files[i].getAbsolutePath().endsWith(".apk")) {// 以.apk这些结尾
                                mRubbishSize+=files[i].length();
                                mApkSize+=files[i].length();
                                filesApk.add(files[i]);
                            }else if(files[i].getAbsolutePath().endsWith(".bak")){
                                mBakSize+=files[i].length();
                                mRubbishSize+=files[i].length();
                                filesBackup.add(files[i]);
                            }else if(files[i].getAbsolutePath().endsWith(".log")){
                                mLogSize+=files[i].length();
                                mSysSize+=files[i].length();
                                mRubbishSize+=files[i].length();
                                filesLog.add(files[i]);
                            }else if(files[i].getAbsolutePath().endsWith(".tmp")||files[i].getAbsolutePath().endsWith(".temp")){
                                mTemSize+=files[i].length();
                                mSysSize+=files[i].length();
                                mRubbishSize+=files[i].length();
                                filesTemp.add(files[i]);
                            }

                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
