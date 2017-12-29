package com.sheep.zk.floatball.bean;

import android.graphics.drawable.Drawable;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sheep.zk.floatball.adapter.RvRubbishCleanAdapter;

import java.io.File;

/**
 * Created by sheep on 2017/12/14.
 */

public class RvRubbishLevel1 extends AbstractExpandableItem<RvRubbishLevel2> implements MultiItemEntity {


    public String mc;
    public String size;
    public Drawable drawable;
    public long bytes;
    public int type;
    public String version;
    public boolean isInstalled;
    public boolean isSelected;
    public File file;

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public RvRubbishLevel1(String mc, String size, Drawable drawable, long bytes, int type, boolean isSelected, File file) {
        this.file = file;
        this.mc = mc;
        this.size = size;
        this.drawable = drawable;
        this.bytes = bytes;
        this.type = type;
        this.isSelected = isSelected;
    }

    public RvRubbishLevel1(String mc, String size, Drawable drawable, long bytes, int type, boolean isSelected) {
        this.mc = mc;
        this.size = size;
        this.drawable = drawable;
        this.bytes = bytes;
        this.type = type;
        this.isSelected = isSelected;
    }

    public RvRubbishLevel1(String mc, String size, Drawable drawable, long bytes, int type, boolean isInstalled, String version, boolean isSelected, File file) {
        this.mc = mc;
        this.size = size;
        this.drawable = drawable;
        this.bytes = bytes;
        this.type = type;
        this.isInstalled = isInstalled;
        this.version = version;
        this.isSelected = isSelected;
        this.file = file;
    }

    @Override
    public int getItemType() {
        return RvRubbishCleanAdapter.TYPE_LEVEL_1;
    }


    @Override
    public int getLevel() {
        return 1;
    }
}
