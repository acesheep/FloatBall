package com.sheep.zk.floatball.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sheep.zk.floatball.R;

/**
 * Created by sheep on 2017/11/15.
 */

public class WaveView extends View{

    private  int width;
    private  int height;

    private Paint mDefaultPaint;
    private Path mDefaultPath;

    private ValueAnimator animator;
    private float animRatio;

    public WaveView(Context context) {
        this(context,null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    private void init() {
        mDefaultPaint=new Paint();
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setColor(getResources().getColor(R.color.bgGreen));
        mDefaultPath=new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    public void performAnimation() {
        animator=ValueAnimator.ofFloat(0.4f,-0.25f,0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animRatio= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                invalidate();
            }
        });
        animator.setDuration(7000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
    boolean flag=false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDefaultPath.moveTo(0,height/20*17);
        mDefaultPath.quadTo(width/2,(height/20*17*animRatio),width,height/20*17);
        mDefaultPath.lineTo(width,height);
        mDefaultPath.lineTo(0,height);
        mDefaultPath.close();
        canvas.drawPath(mDefaultPath,mDefaultPaint);
        mDefaultPath.reset();
        if(!flag){
            performAnimation();
            flag=true;
        }
    }
}
