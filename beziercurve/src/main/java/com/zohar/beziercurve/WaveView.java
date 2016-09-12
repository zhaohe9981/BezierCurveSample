package com.zohar.beziercurve;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by 小牛冲冲冲 on 2016/9/9.
 * Email:zhao_zhaohe@163.com
 *
 * 波浪view 仿百度贴吧加载中
 * （原理图查看app/picture/波浪图原理.png）
 * 参考 ： http://blog.csdn.net/z82367825/article/details/51599245
 */
public class WaveView extends View {

    private static final String TAG = "WaveView";

    private int defaultWidth = 50;
    private int defaultHeight = 50;

    //---------------------------
    private float width ;
    private float height;
    private float realWidth ;

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;


    /**
     * 波峰距离控件顶部的高度
     */
    private float waveTop = 100.0f;
    /**
     * 波峰的高度
     */
    private float wavePeak = 40.0f;

    //波峰，波谷的高度
    private float wavePeakHeight;
    private float waveTroughHeight;
    //波浪X轴距离控件顶部的高度
    private float waveXHeight;

    //波浪与横坐标的交叉点（y=0）
    private PointF pointOne;
    private PointF pointTwo;
    private PointF pointThree;
    private PointF pointFour;
    private PointF pointFive;
    //四个控制点
    private PointF controlOne;
    private PointF controlTwo;
    private PointF controlThree;
    private PointF controlFour;

    /**
     * 画笔
     */
    private Paint mPaint = null;
    /**
     * 波浪线
     */
    private Path mPath = null;


    private boolean isInit = false;
    private boolean isRunning = false;
    /**
     * 系统版本号
     */
    private int osversion;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5);
        mPath = new Path();
        osversion = Build.VERSION.SDK_INT;
        Log.i(TAG, "WaveView_init");
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
            paddingLeft = getPaddingLeft();
            paddingTop = getPaddingTop();
            paddingRight = getPaddingRight();
            paddingBottom = getPaddingBottom();
            width = w;
            height = h;
            realWidth = width - paddingLeft - paddingRight;
            wavePeakHeight = paddingTop + waveTop;
            waveTroughHeight = wavePeakHeight + wavePeak*2;
            waveXHeight = wavePeakHeight + wavePeak;
            reset();
            isInit = true;

            //Thread相关的数据
            everyMove = realWidth / 50.0f;
            baseX = -realWidth;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 初始化 波浪的初始化状态
     */
    private void reset(){
        updatePoint(-realWidth);//回到初十状态。（查看app/picture/波浪图原理.png）
        Log.i(TAG, "WaveView_reset");
    }
    /**
     * 更新点的坐标
     */
    private void updatePoint( float pointOneX){
        pointOne = new PointF(pointOneX,waveXHeight);
        pointTwo = new PointF(pointOne.x + (realWidth/2.0f),waveXHeight);
        pointThree = new PointF(pointOne.x + realWidth,waveXHeight);
        pointFour = new PointF(pointOne.x + (realWidth*3/2.0f),waveXHeight);
        pointFive = new PointF(pointOne.x + realWidth*2,waveXHeight);

        controlOne = new PointF(pointOne.x + (realWidth/4.0f),wavePeakHeight);
        controlTwo = new PointF(pointOne.x + (realWidth*3/4.0f),waveTroughHeight);
        controlThree = new PointF(pointOne.x + (realWidth*5/4.0f),wavePeakHeight);
        controlFour = new PointF(pointOne.x + (realWidth*7/4.0f),waveTroughHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!isInit)
            return;
        if(!isRunning){
            startAnim();
        }
        mPath.reset();
        mPath.moveTo(pointOne.x, pointOne.y);
        mPath.quadTo(controlOne.x, controlOne.y, pointTwo.x, pointTwo.y);
        mPath.quadTo(controlTwo.x,controlTwo.y,pointThree.x,pointThree.y);
        mPath.quadTo(controlThree.x, controlThree.y, pointFour.x, pointFour.y);
        mPath.quadTo(controlFour.x, controlFour.y, pointFive.x, pointFive.y);
        mPath.lineTo(pointFive.x, height - paddingBottom);
        mPath.lineTo(pointOne.x, height - paddingBottom);
        canvas.drawPath(mPath, mPaint);
    }

    private void startAnim(){
        Log.i(TAG, "WaveView_startAnim");
        isRunning = true;
        if (Build.VERSION.SDK_INT >= 11){
            Log.i(TAG, "WaveView_startAnim1(api>=11)");
            startAnim1();
        }else{
            Log.i(TAG, "WaveView_startAnim2(api<11)");
            startAnim2();
        }
    }

    @TargetApi(11)
    private void startAnim1(){
        ValueAnimator animator = ValueAnimator.ofFloat(pointOne.x,0);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updatePoint((float) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * startAnim2()时不能在子线程中更新，所以要使用handler
     */
    private Handler updateView  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                invalidate();
            }
        }
    };
    /**
     * 每次移动的距离
     */
    float everyMove ;
    /**
     * 最开始，pointOne的X坐标
     */
    float baseX ;

    /**
     * 系统版本低于11不能使用属性动画
     */
    private void startAnim2(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (baseX >= paddingLeft - everyMove){
                        baseX = -realWidth;
                    }
                    baseX += everyMove;
                    updatePoint(baseX);
                    updateView.sendEmptyMessage(1);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
