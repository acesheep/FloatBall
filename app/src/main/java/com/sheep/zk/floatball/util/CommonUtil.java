package com.sheep.zk.floatball.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.bean.StorageSize;

import java.util.List;

/**
 * Created by sheep on 2017/1/9.
 */

public class CommonUtil {
    public static String getDeviceId(Context context) {
        String deviceId = "";
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            deviceId = tm.getDeviceId();
        }
        return deviceId;
    }

    public static PackageInfo getAllApps(Context context, String app_flag_1, String app_flag_2) {
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);
            if (pak.packageName.contains(app_flag_1) || pak.packageName.contains(app_flag_2)) {
                return pak;
            }

        }
        return null;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if (isWifiConnected(context)) {
            return false;
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;

    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }


    // storage, G M K B
    public static StorageSize convertStorageSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        StorageSize sto = new StorageSize();
        if (size >= gb) {

            sto.suffix = "GB";
            sto.value = (float) size / gb;
            return sto;
        } else if (size >= mb) {

            sto.suffix = "MB";
            sto.value = (float) size / mb;

            return sto;
        } else if (size >= kb) {


            sto.suffix = "KB";
            sto.value = (float) size / kb;

            return sto;
        } else {
            sto.suffix = "B";
            sto.value = (float) size;

            return sto;
        }
    }
}
