package com.sheep.zk.floatball.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by sheep on 2017/11/23.
 */

public class AppInfo {
    private int id;
    private boolean isFilterProcess;
    private String packageName;
    private Drawable icon;
    private String name;
    private int memorySize;
    private boolean isSystemProcess;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsFilterProcess() {
        return isFilterProcess;
    }

    public void setIsFilterProcess(boolean isFilterProcess) {
        this.isFilterProcess = isFilterProcess;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public boolean getIsSystemProcess() {
        return isSystemProcess;
    }

    public void setIsSystemProcess(boolean systemProcess) {
        isSystemProcess = systemProcess;
    }
}
