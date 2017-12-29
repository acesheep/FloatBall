package com.sheep.zk.floatball.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.bean.CacheListItem;
import com.sheep.zk.floatball.bean.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

public class LvPowerSavingAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    public List<ProcessInfo> mlistAppInfo;
    LayoutInflater infater = null;
    private Context mContext;
    public static List<Integer> clearIds;

    public LvPowerSavingAdapter(Context context, List<ProcessInfo> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        clearIds = new ArrayList<Integer>();
        this.mlistAppInfo = apps;
    }

    @Override
    public int getCount() {
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = infater.inflate(R.layout.item_listview_rublish_clean,
                    parent, false);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name);
            holder.size = (TextView) convertView
                    .findViewById(R.id.app_size);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ProcessInfo item= (ProcessInfo) getItem(position);
        if (item != null) {
            holder.appIcon.setImageDrawable(item.getIcon());
            holder.appName.setText(item.getName());
            holder.size.setText(Formatter.formatShortFileSize(mContext, item.getSize()));
            holder.packageName = item.getPackName();
        }


        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (viewHolder != null && viewHolder.packageName != null) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + viewHolder.packageName));

            mContext.startActivity(intent);
        }
    }

    class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView size;


        String packageName;
    }

}
