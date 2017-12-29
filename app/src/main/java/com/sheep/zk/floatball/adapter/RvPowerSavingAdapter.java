package com.sheep.zk.floatball.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.base.BaseApplication;
import com.sheep.zk.floatball.bean.ProcessInfo;
import com.sheep.zk.floatball.util.ToastTool;

import java.util.List;


/**
 * Created by sheep on 2017/12/7.
 */



/**
 * RvPowerSavingAdapter实现可拖拽与侧滑
 */
public class RvPowerSavingAdapter extends BaseItemDraggableAdapter<ProcessInfo,BaseViewHolder> {
    private Context mContext;
    private ActivityManager am;
    private List<ProcessInfo> mProcessInfos;
    private ActivityInfo launcherInfo;
    public RvPowerSavingAdapter(int layoutResId, List data, Context context) {
        super(layoutResId, data);
        mContext = context;
        mProcessInfos=data;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        launcherInfo = new Intent(Intent.ACTION_MAIN).addCategory(
                Intent.CATEGORY_HOME).resolveActivityInfo(context.getPackageManager(), 0);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ProcessInfo item) {
        helper.setText(R.id.app_name, item.getName());
        helper.setText(R.id.app_size, Formatter.formatShortFileSize(mContext, item.getSize()));
        helper.setImageDrawable(R.id.app_icon, item.getIcon());
        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BaseApplication.isOnItemClick = true;
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + item.getPackName()));
                mContext.startActivity(intent);
            }
        });
        helper.getView(R.id.qingli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getPackName().contains(mContext.getPackageName())){
                    ToastTool.getInstance().shortLength(mContext, "当前进程无法被清理！", true);
                }else if(item.getPackName().contains(launcherInfo.packageName)||item.getPackName().contains("com.jgahaid")
                        ||item.getPackName().contains("com.wx")){
                    ToastTool.getInstance().shortLength(mContext, item.getName()+"无法被清理！", true);
                }else {
                    am.killBackgroundProcesses(item.getPackName());
                    ToastTool.getInstance().shortLength(mContext, item.getName()+"被清理掉了！", true);
                    mProcessInfos.remove(item);
                    notifyDataSetChanged();
                }
            }
        });
    }


}
//public class RvPowerSavingAdapter extends BaseQuickAdapter<ProcessInfo, BaseViewHolder> {
//    private Context mContext;
//
//    public RvPowerSavingAdapter(int layoutResId, List data, Context context) {
//        super(layoutResId, data);
//        mContext = context;
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, final ProcessInfo item) {
//        helper.setText(R.id.app_name, item.getName());
//        helper.setText(R.id.app_size, Formatter.formatShortFileSize(mContext, item.getSize()));
//        helper.setImageDrawable(R.id.app_icon, item.getIcon());
//        helper.getView(R.id.content).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BaseApplication.isOnItemClick = true;
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + item.getPackName()));
//                mContext.startActivity(intent);
//                ToastTool.getInstance().shortLength(mContext, "啦啦啦啦！", true);
//            }
//        });
//        helper.getView(R.id.qingli).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ToastTool.getInstance().shortLength(mContext, "清理掉！", true);
//            }
//        });
//    }
//
//
//}