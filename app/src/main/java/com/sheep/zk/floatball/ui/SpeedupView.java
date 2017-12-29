package com.sheep.zk.floatball.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import com.orhanobut.logger.Logger;
import com.sheep.zk.floatball.R;

/**
 * Created by sheep on 2017/11/27.
 */

public class SpeedupView extends android.support.v7.widget.AppCompatImageView {
    Bitmap bitmap;
    private int width;
    private int height;
    private ValueAnimator animatorScale;
    private float animScaleRatio;
    private ValueAnimator animatorRotate;
    private float animRotateRatio;
    private static final float[] ROTATE_FACTOR = new float[]{0, 0.28f, 0.72f, 1.0f};

    public SpeedupView(Context context) {
        this(context, null);
    }

    public SpeedupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_speed_up_anim);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(animScaleRatio, animScaleRatio, width / 2, height / 2);
        canvas.rotate(360*animRotateRatio,width/2,height/2);
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, src, new Rect(0, 0, width, height), null);
        canvas.restore();

    }

    public void performAnimation(final Context context) {
        animatorScale = ValueAnimator.ofFloat(1f,0.5f, 1f);
        animatorScale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animScaleRatio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animatorScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                invalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Intent intent=new Intent();
                intent.setAction("speedup.anim");
                context.sendBroadcast(intent);
            }
        });
        animatorScale.setDuration(7000);
        animatorScale.setInterpolator(new LinearInterpolator());

        animatorRotate=ValueAnimator.ofFloat(0f,20f);
        animatorRotate.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animRotateRatio= (float) animation.getAnimatedValue();
            }
        });
        animatorRotate.setDuration(7000);
        animatorRotate.setInterpolator(new rotateInterpolator());

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animatorScale,animatorRotate);
        animatorSet.start();
    }

    private class rotateInterpolator implements TimeInterpolator{

        @Override
        public float getInterpolation(float input) {
                return input*input;
        }
    }
}