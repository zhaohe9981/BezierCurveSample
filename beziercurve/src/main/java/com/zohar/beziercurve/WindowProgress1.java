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

/**
 * Created by 小牛冲冲冲 on 2016/9/20.
 * Email:zhao_zhaohe@163.com
 *
 * 参考： http://blog.csdn.net/zhangml0522/article/details/52556418
 */
public class WindowProgress1 extends View {

    private static final String TAG = "WindowProgress1";

    private int defaultWidth = 50;
    private int defaultHeight = 50;

    private float strokeWidth = 15.0f;
    private boolean isInit = false;
    private boolean isStart = false;
    private RectF mRectF = null;
    private float topPointX;//顶点x
    private float topPointY;//顶点y

    private Paint mPaint = null;
    private Path mPath = null;
    private Path dst = null;
    private PathMeasure pathMeasure = null;
    private float pathLength;//path的长度

    private float t;

    public WindowProgress1(Context context) {
        super(context);
        init();
    }

    public WindowProgress1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WindowProgress1(Context context, AttributeSet attrs, int defStyleAttr) {
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
        dst = new Path();
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
            mPath.addArc(mRectF,-90.0f,359.9f);
            pathMeasure = new PathMeasure(mPath,false);
            pathLength = pathMeasure.getLength();
            topPointX = w/2.0f;
            topPointY = getPaddingTop()+strokeWidth;
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

            dst.reset();
//            dst.lineTo(0,0);//如果activity 没有关闭硬件加速，使用这句。
            float x;
            float s = pathLength;
            float y;
            int num = (int) (t/0.05);

            switch (num) {
                default:
                case 3:
                    x = t - 0.15f*(1-t);
                    y = -x*x*s + 2*s*x;
                    pathMeasure.getSegment(y, y + 1, dst, true);
                case 2:
                    x = t - 0.10f*(1-t);
                    y = -x*x*s + 2*s*x;
                    pathMeasure.getSegment(y, y + 1, dst, true);
                case 1:
                    x = t - 0.05f*(1-t);
                    y = -x*x*s + 2*s*x;
                    pathMeasure.getSegment(y, y + 1, dst, true);
                case 0:
                    x = t;
                    y = -x*x*s + 2*s*x;
                    pathMeasure.getSegment(y, y + 1, dst, true);
                    break;
            }
            canvas.drawPath(dst,mPaint);
            if (t > 0.95f){
                canvas.drawPoint(topPointX,topPointY,mPaint);
            }
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
                Log.i(TAG,"t = "+t);
                invalidate();
            }
        });
        animator.start();
    }
}
