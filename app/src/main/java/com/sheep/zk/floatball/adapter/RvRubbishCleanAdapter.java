package com.sheep.zk.floatball.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.R;
import com.sheep.zk.floatball.bean.RvRubbishLevel1;
import com.sheep.zk.floatball.bean.RvRubbishLevel2;
import com.sheep.zk.floatball.bean.RvRubbishPerson;
import com.sheep.zk.floatball.util.Constant;
import com.sheep.zk.floatball.util.PopupCleanRubbish;
import com.sheep.zk.floatball.util.ToastTool;
import com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity;

import java.io.File;
import java.util.List;

import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.filesApk;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.filesBackup;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.filesDelete;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.filesLog;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.filesTemp;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.listApk;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.listBac;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.listLog;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.listTemp;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.rvRubbishLevel1Log;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.rvRubbishLevel1Temp;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.rvRubbishPersonApk;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.rvRubbishPersonBac;
import static com.sheep.zk.floatball.view.impl.RvRubbishCleanActivity.rvRubbishPersonSys;

/**
 * Created by sheep on 2017/12/11.
 */

public class RvRubbishCleanAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_PERSON = 0;
    public static final int TYPE_LEVEL_1 = 1;
    public static final int TYPE_LEVEL_2 = 2;
    private Context mContext;
    PopupCleanRubbish popupCleanRubbish;

    public RvRubbishCleanAdapter(List data, Context context) {
        super(data);
        addItemType(TYPE_PERSON, R.layout.item_rvrubbish_person);
        addItemType(TYPE_LEVEL_1, R.layout.item_rvrubbish_level1);
        addItemType(TYPE_LEVEL_2, R.layout.item_rvrubbish_level2);
        mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {

            case TYPE_PERSON:
                final RvRubbishPerson rvRubbishPerson = (RvRubbishPerson) item;
                helper.setText(R.id.tv_rvrubbish_person_mc, rvRubbishPerson.mc)
                        .setText(R.id.tv_rvrubbish_person_size, rvRubbishPerson.size)
                        .setImageResource(R.id.iv_rvrubbish_person_arrow, rvRubbishPerson.isExpanded() ? R.mipmap.icon_on_orange : R.mipmap.icon_under_orange)
                        .addOnClickListener(R.id.iv_rvrubbish_person_choose);
                helper.getView(R.id.iv_rvrubbish_person_arrow).setVisibility(rvRubbishPerson.bytes <= 0 ? View.GONE : View.VISIBLE);
                helper.getView(R.id.iv_rvrubbish_person_choose).setVisibility(rvRubbishPerson.bytes <= 0 ? View.INVISIBLE : View.VISIBLE);
                helper.setImageResource(R.id.iv_rvrubbish_person_choose, rvRubbishPerson.isSelected ? R.mipmap.icon_orange_sure : R.mipmap.ic_circular_rect);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int pos = helper.getAdapterPosition();
                        if (rvRubbishPerson.isExpanded()) {
                            collapse(pos);
                            notifyDataSetChanged();

                        } else {

                            expand(pos);
                            notifyDataSetChanged();
                        }
                    }

                });
                helper.getView(R.id.iv_rvrubbish_person_choose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rvRubbishPerson.isSelected()) {
                            rvRubbishPerson.setSelected(false);
                            if (rvRubbishPerson.type == Constant.ISXITONGLAJI) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonSys.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(false);
                                        List<RvRubbishLevel2> rvRubbishLevel2s = rvRubbishLevel1s.get(i).getSubItems();
                                        if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                            for (int j = 0; j < rvRubbishLevel2s.size(); j++) {
                                                rvRubbishLevel2s.get(j).setSelected(false);
                                            }
                                        }
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());

                            } else if (rvRubbishPerson.type == Constant.ISKONGYUBEIFEN) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonBac.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(false);
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesBackup) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishPerson.type == Constant.ISANZHUANGBAO) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonApk.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(false);
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesApk) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        } else {
                            rvRubbishPerson.setSelected(true);
                            if (rvRubbishPerson.type == Constant.ISXITONGLAJI) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonSys.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(true);
                                        List<RvRubbishLevel2> rvRubbishLevel2s = rvRubbishLevel1s.get(i).getSubItems();
                                        if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                            for (int j = 0; j < rvRubbishLevel2s.size(); j++) {
                                                rvRubbishLevel2s.get(j).setSelected(true);
                                            }
                                        }
                                    }
                                }

                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());

                            } else if (rvRubbishPerson.type == Constant.ISKONGYUBEIFEN) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonBac.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {

                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(true);
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesBackup) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesBackup) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishPerson.type == Constant.ISANZHUANGBAO) {
                                List<RvRubbishLevel1> rvRubbishLevel1s = RvRubbishCleanActivity.rvRubbishPersonApk.getSubItems();
                                if (rvRubbishLevel1s != null && rvRubbishLevel1s.size() > 0) {

                                    for (int i = 0; i < rvRubbishLevel1s.size(); i++) {
                                        rvRubbishLevel1s.get(i).setSelected(true);
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesApk) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesApk) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        }
                        notifyDataSetChanged();
                    }
                });

                break;
            case TYPE_LEVEL_1:
                final RvRubbishLevel1 rvRubbishLevel1 = (RvRubbishLevel1) item;
                if (rvRubbishLevel1.type == Constant.ISAPK) {
                    helper.setText(R.id.tv_rvrubbish_levelone_apk_mc, rvRubbishLevel1.mc)
                            .setText(R.id.tv_rvrubbish_levelone_apk_version, rvRubbishLevel1.isInstalled ? "[已安装]" + rvRubbishLevel1.version : "[未安装]" + rvRubbishLevel1.version)
                            .setText(R.id.tv_rvrubbish_levelone_size, rvRubbishLevel1.size)
                            .setImageDrawable(R.id.iv_item_levelone_icon, rvRubbishLevel1.drawable);
                    helper.getView(R.id.ll_rvrubbish_levelone).setVisibility(View.VISIBLE);
                    helper.getView(R.id.iv_rvrubbish_levelone_arrow).setVisibility(View.GONE);
                    helper.getView(R.id.tv_rvrubbish_levelone_mc).setVisibility(View.GONE);
                } else {
                    helper.setText(R.id.tv_rvrubbish_levelone_mc, rvRubbishLevel1.mc)
                            .setText(R.id.tv_rvrubbish_levelone_size, rvRubbishLevel1.size)
                            .setImageDrawable(R.id.iv_item_levelone_icon, rvRubbishLevel1.drawable)
                            .setImageResource(R.id.iv_rvrubbish_levelone_arrow, rvRubbishLevel1.isExpanded() ? R.mipmap.icon_on_orange : R.mipmap.icon_under_orange);
                    helper.getView(R.id.ll_rvrubbish_levelone).setVisibility(View.GONE);
                    helper.getView(R.id.iv_rvrubbish_levelone_arrow).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_rvrubbish_levelone_mc).setVisibility(View.VISIBLE);
                }
                if (rvRubbishLevel1.type == Constant.ISBAC) {
                    helper.getView(R.id.iv_rvrubbish_levelone_arrow).setVisibility(View.GONE);
                } else if (rvRubbishLevel1.type == Constant.ISSYSTEMP || rvRubbishLevel1.type == Constant.ISSYSLOG) {
                    helper.getView(R.id.iv_rvrubbish_levelone_arrow).setVisibility(rvRubbishLevel1.bytes <= 0 ? View.GONE : View.VISIBLE);
                    helper.getView(R.id.iv_rvrubbish_levelone_choose).setVisibility(rvRubbishLevel1.bytes <= 0 ? View.INVISIBLE : View.VISIBLE);
                }
                helper.setImageResource(R.id.iv_rvrubbish_levelone_choose, rvRubbishLevel1.isSelected ? R.mipmap.icon_orange_sure : R.mipmap.ic_circular_rect);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (rvRubbishLevel1.type == Constant.ISAPK || rvRubbishLevel1.type == Constant.ISBAC) {
                            popupCleanRubbish = new PopupCleanRubbish(mContext);
                            popupCleanRubbish.tvTitle.setText(rvRubbishLevel1.getMc());
                            popupCleanRubbish.tvContent.setText("大小：" + rvRubbishLevel1.size + "\n\n路径：" + rvRubbishLevel1.file.getAbsolutePath());
                            popupCleanRubbish.setBackPressEnable(true);
                            popupCleanRubbish.setPopupWindowFullScreen(true);
                            popupCleanRubbish.showPopupWindow();
                        } else {
                            if (rvRubbishLevel1.isExpanded()) {

                                collapse(pos);
                            } else {

                                expand(pos);
                            }
                        }
                    }
                });
                helper.getView(R.id.iv_rvrubbish_levelone_choose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rvRubbishLevel1.isSelected()) {
                            rvRubbishLevel1.setSelected(false);
                            if (rvRubbishLevel1.type == Constant.ISSYSTEMP) {
                                rvRubbishPersonSys.setSelected(false);
                                List<RvRubbishLevel2> rvRubbishLevel2s = RvRubbishCleanActivity.rvRubbishLevel1Temp.getSubItems();
                                if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel2s.size(); i++) {
                                        rvRubbishLevel2s.get(i).setSelected(false);
                                    }
                                }
                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());

                            } else if (rvRubbishLevel1.type == Constant.ISSYSLOG) {
                                rvRubbishPersonSys.setSelected(false);
                                List<RvRubbishLevel2> rvRubbishLevel2s = RvRubbishCleanActivity.rvRubbishLevel1Log.getSubItems();
                                if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel2s.size(); i++) {
                                        rvRubbishLevel2s.get(i).setSelected(false);
                                    }
                                }

                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());

                            } else if (rvRubbishLevel1.type == Constant.ISBAC) {
                                rvRubbishPersonBac.setSelected(false);
                                RvRubbishCleanActivity.filesDelete.remove(rvRubbishLevel1.file);
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishLevel1.type == Constant.ISAPK) {
                                rvRubbishPersonApk.setSelected(false);
                                RvRubbishCleanActivity.filesDelete.remove(rvRubbishLevel1.file);
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        } else {
                            rvRubbishLevel1.setSelected(true);
                            if (rvRubbishLevel1.type == Constant.ISSYSTEMP) {
                                if (rvRubbishLevel1Log.isSelected) {
                                    rvRubbishPersonSys.setSelected(true);
                                }
                                List<RvRubbishLevel2> rvRubbishLevel2s = RvRubbishCleanActivity.rvRubbishLevel1Temp.getSubItems();
                                if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel2s.size(); i++) {
                                        rvRubbishLevel2s.get(i).setSelected(true);
                                    }
                                }


                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesTemp) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());

                            } else if (rvRubbishLevel1.type == Constant.ISSYSLOG) {
                                if (rvRubbishLevel1Temp.isSelected) {
                                    rvRubbishPersonSys.setSelected(true);
                                }
                                List<RvRubbishLevel2> rvRubbishLevel2s = RvRubbishCleanActivity.rvRubbishLevel1Log.getSubItems();
                                if (rvRubbishLevel2s != null && rvRubbishLevel2s.size() > 0) {
                                    for (int i = 0; i < rvRubbishLevel2s.size(); i++) {
                                        rvRubbishLevel2s.get(i).setSelected(true);
                                    }
                                }


                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.remove(file);
                                }
                                for (File file : RvRubbishCleanActivity.filesLog) {
                                    RvRubbishCleanActivity.filesDelete.add(file);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishLevel1.type == Constant.ISBAC) {
                                RvRubbishCleanActivity.filesDelete.add(rvRubbishLevel1.file);
                                boolean flag = true;
                                for (int i = 0; i < listBac.size(); i++) {
                                    if (!listBac.get(i).isSelected) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    rvRubbishPersonBac.setSelected(true);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishLevel1.type == Constant.ISAPK) {
                                RvRubbishCleanActivity.filesDelete.add(rvRubbishLevel1.file);
                                boolean flag = true;
                                for (int i = 0; i < listApk.size(); i++) {
                                    if (!listApk.get(i).isSelected) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    rvRubbishPersonApk.setSelected(true);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
            case TYPE_LEVEL_2:
                final RvRubbishLevel2 rvRubbishLevel2 = (RvRubbishLevel2) item;
                helper.setText(R.id.tv_rvrubbish_leveltwo_mc, rvRubbishLevel2.mc);
                helper.setText(R.id.tv_rvrubbish_leveltwo_size, rvRubbishLevel2.size);
                helper.setImageDrawable(R.id.iv_item_leveltwo_icon, rvRubbishLevel2.drawable);
                helper.setImageResource(R.id.iv_rvrubbish_leveltwo_choose, rvRubbishLevel2.isSelected ? R.mipmap.icon_orange_sure : R.mipmap.ic_circular_rect);

                helper.getView(R.id.iv_rvrubbish_leveltwo_choose).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rvRubbishLevel2.isSelected()) {
                            rvRubbishLevel2.setSelected(false);
                            if (rvRubbishLevel2.type == Constant.ISTEMP) {
                                rvRubbishLevel1Temp.setSelected(false);
                                rvRubbishPersonSys.setSelected(false);
                                RvRubbishCleanActivity.filesDelete.remove(rvRubbishLevel2.file);
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishLevel2.type == Constant.ISLOG) {
                                rvRubbishLevel1Log.setSelected(false);
                                rvRubbishPersonSys.setSelected(false);
                                RvRubbishCleanActivity.filesDelete.remove(rvRubbishLevel2.file);
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        } else {
                            rvRubbishLevel2.setSelected(true);
                            if (rvRubbishLevel2.type == Constant.ISTEMP) {
                                RvRubbishCleanActivity.filesDelete.add(rvRubbishLevel2.file);
                                boolean flag = true;
                                for (int i = 0; i < listTemp.size(); i++) {
                                    if (!listTemp.get(i).isSelected) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    rvRubbishLevel1Temp.setSelected(true);
                                }
                                if (rvRubbishLevel1Temp.isSelected && rvRubbishLevel1Log.isSelected) {
                                    rvRubbishPersonSys.setSelected(true);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            } else if (rvRubbishLevel2.type == Constant.ISLOG) {
                                RvRubbishCleanActivity.filesDelete.add(rvRubbishLevel2.file);
                                boolean flag = true;
                                for (int i = 0; i < listLog.size(); i++) {
                                    if (!listLog.get(i).isSelected) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    rvRubbishLevel1Log.setSelected(true);
                                }
                                if (rvRubbishLevel1Temp.isSelected && rvRubbishLevel1Log.isSelected) {
                                    rvRubbishPersonSys.setSelected(true);
                                }
                                Logger.e("filesDelete=" + filesDelete.size());
                                Logger.e("filesLog=" + filesLog.size());
                                Logger.e("filesTemp=" + filesTemp.size());
                                Logger.e("filesBac=" + filesBackup.size());
                                Logger.e("filesApk=" + filesApk.size());
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupCleanRubbish = new PopupCleanRubbish(mContext);
                        popupCleanRubbish.tvTitle.setText(rvRubbishLevel2.getMc());
                        popupCleanRubbish.tvContent.setText("大小：" + rvRubbishLevel2.size + "\n\n路径：" + rvRubbishLevel2.file.getAbsolutePath());
                        popupCleanRubbish.setBackPressEnable(true);
                        popupCleanRubbish.setPopupWindowFullScreen(true);
                        popupCleanRubbish.showPopupWindow();
                    }
                });
                break;
        }
    }

    public boolean exit() {
        if (popupCleanRubbish != null && popupCleanRubbish.isShowing()) {
            popupCleanRubbish.dismiss();
            return false;
        } else {
            return true;
        }
    }
}
