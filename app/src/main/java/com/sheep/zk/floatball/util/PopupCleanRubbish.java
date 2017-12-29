package com.sheep.zk.floatball.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.sheep.zk.floatball.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by sheep on 2017/12/22.
 */

public class PopupCleanRubbish extends BasePopupWindow {
    private View popupView;
    public TextView tvTitle;
    public TextView tvCancel;
    public TextView tvContent;

    public PopupCleanRubbish(Context context) {
        super(context);
        tvTitle = (TextView) popupView.findViewById(R.id.tv_title);
        tvContent = (TextView) popupView.findViewById(R.id.tv_content);
        tvCancel = (TextView) popupView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    protected Animation initShowAnimation() {
        return getDefaultScaleAnimation();
    }

    @Override
    protected Animation initExitAnimation() {
        return getDefaultScaleAnimation(false);
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    @Override
    public View onCreatePopupView() {
        popupView = createPopupById(R.layout.popup_rubbish_clean_item);
        return popupView;
    }

    @Override
    public View initAnimaView() {
        return popupView.findViewById(R.id.popup_anima);
    }
}
