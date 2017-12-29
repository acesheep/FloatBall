package com.sheep.zk.floatball.bean;

import android.graphics.drawable.Drawable;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sheep.zk.floatball.adapter.RvRubbishCleanAdapter;

import java.io.File;

/**
 * Created by sheep on 2017/12/14.
 */


public class RvRubbishLevel2 implements MultiItemEntity {

    public String mc;
    public String size;
    public Drawable drawable;
    public long bytes;
    public boolean isSelected;
    public File file;
    public int type;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public RvRubbishLevel2(String mc, String size, Drawable drawable, long bytes, boolean isSelected,File file,int type) {
        this.mc = mc;
        this.file=file;
        this.size = size;
        this.drawable = drawable;
        this.bytes = bytes;
        this.isSelected = isSelected;
        this.type=type;
    }

    @Override
    public int getItemType() {
        return RvRubbishCleanAdapter.TYPE_LEVEL_2;
    }


}
