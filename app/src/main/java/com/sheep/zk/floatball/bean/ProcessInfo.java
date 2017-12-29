package com.sheep.zk.floatball.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by sheep on 2017/12/6.
 */

public class ProcessInfo {
    public String name;  //进程的名字
    public Drawable icon;  //进程的图标
    public String packName;  //进程的包名
    public boolean isSystem;   //是否为系统应用
    public long size;         //进程的占用的内存

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
