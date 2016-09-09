package com.zohar.beziercurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 小牛冲冲冲 on 2016/9/9.
 * Email:zhao_zhaohe@163.com
 *
 * 波浪view 仿百度贴吧加载中
 */
public class WaveView extends View {

    private static final String TAG = "WaveView";

    private int defaultWidth = 50;
    private int defaultHeight = 50;
    /**
     * 波浪数
     * 一个波浪数对应两个极值。即一个凸和一个凹
     */
    private int waveCount = 1;
    /**
     * 宽度被分的段数
     */
    private int widthParts = waveCount*4;

    /**
     * 画笔
     */
    private Paint mPaint = null;
    /**
     * 波浪线
     */
    private Path mPath = null;

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int realHeigt = height - paddingTop - paddingBottom;
        int realWidth = width - paddingLeft - paddingRight;

        mPath.moveTo(paddingLeft, height / 2);
        mPath.quadTo((realWidth - paddingLeft - paddingRight) / 4, height / 3,(width - paddingLeft - paddingRight) / 2, height/ 2);
        mPath.quadTo((width - paddingLeft - paddingRight) *3/ 4, height *2/ 3, width - paddingRight, height / 2);
        mPath.lineTo(width - paddingRight, height -paddingBottom);
        mPath.lineTo(paddingLeft,height);
        canvas.drawPath(mPath,mPaint);
    }
}
