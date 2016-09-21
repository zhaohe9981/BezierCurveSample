package com.zohar.beziercurve;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;

/**
 * Created by 小牛冲冲冲 on 2016/9/20.
 * Email:zhao_zhaohe@163.com
 *
 * 模仿window10 dialog 效果
 */
public class WindowProgress extends View{

    private static final String TAG = "WindowProgress";

    private int defaultWidth = 50;
    private int defaultHeight = 50;
    private float strokeWidth = 15.0f;
    private boolean isInit = false;
    private boolean isStart = false;
    private RectF mRectF = null;

    private Paint mPaint = null;
    private Path mPath = null;
    private PathMeasure pathMeasure = null;

    private float t;

    public WindowProgress(Context context) {
        super(context);
        init();
    }

    public WindowProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WindowProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        Log.i(TAG, TAG + "初始化！");
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
        pathMeasure = new PathMeasure();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, widthSize);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(heightSize, heightSize);
        } else {
            setMeasuredDimension(defaultWidth, defaultHeight);
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if(!isInit){
            mRectF = new RectF(getPaddingLeft()+strokeWidth,getPaddingTop()+strokeWidth,w-getPaddingRight()-strokeWidth,h-getPaddingBottom()-strokeWidth);
            isInit = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isStart){
            startAnim();
        }else{
            mPath.reset();
            mPath.addArc(mRectF, -90 + 360 * t, 1);
            if(t >= 0.05f){
                mPath.addArc(mRectF, -90 + 360 * (t - 0.05f)/(1.0f - 0.05f), 1);
            }
            if (t >= 0.10f){
                mPath.addArc(mRectF, -90 + 360 * (t - 0.10f)/ (1.0f - 0.10f), 1);
            }
            if(t >= 0.15f){
                mPath.addArc(mRectF, -90 + 360*(t-0.15f)/(1.0f-0.15f),1);
            }
            canvas.drawPath(mPath,mPaint);
        }
    }

    @TargetApi(11)
    private void startAnim(){
        isStart = true;
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(3000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                t = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }
}
