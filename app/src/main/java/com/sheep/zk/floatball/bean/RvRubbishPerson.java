package com.sheep.zk.floatball.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.sheep.zk.floatball.adapter.RvRubbishCleanAdapter;

import static android.R.attr.name;

/**
 * Created by sheep on 2017/12/14.
 */

public class RvRubbishPerson extends AbstractExpandableItem<RvRubbishLevel1> implements MultiItemEntity {

    public String mc;
    public String size;
    public long bytes;
    public boolean isSelected;
    public int type;

    public RvRubbishPerson(String mc, String size, long bytes, boolean isSelected, int type) {
        this.mc = mc;
        this.size = size;
        this.bytes = bytes;
        this.isSelected = isSelected;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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

    @Override
    public int getItemType() {
        return RvRubbishCleanAdapter.TYPE_PERSON;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
