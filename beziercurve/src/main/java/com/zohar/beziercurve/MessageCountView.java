package com.zohar.beziercurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 小牛冲冲冲 on 2016/9/13.
 * Email:zhao_zhaohe@163.com
 * <p/>
 * 类似于 qq消息个数提示，可以滑动消失
 * 参考来源：https://github.com/lovejjfg/Circle
 */
public class MessageCountView extends View {

    private static final String TAG = "MessageCountView";
    private static final int defaultWidth = 50;//默认宽度
    private static final int defaultHeight = 50;//默认高度

    /**
     * 可以正常拖拽的最大距离
     */
    private static final float maxDistance = 200;

    /**
     * 正常初始化状态
     */
    private static final int CURRENT_DRAG_STATE_NORMAL = 1;
    /**
     * 抬起恢复状态
     */
    private static final int CURRENT_DRAG_STATE_UP_BACK = 2;
    /**
     * 抬起消失状态
     */
    private static final int CURRENT_DRAG_STATE_UP_BREAK = 3;
    /**
     * 消失状态
     */
    private static final int CURRENT_DRAG_STATE_BREAK = 4;
    /**
     * 当前状态
     */
    private int currentState = CURRENT_DRAG_STATE_NORMAL;


    /**
     * 标记。true 表示 msgCircle在以oriCircle为圆心的 第1，3象限
     */
    private boolean flag = false;
    /**
     * dx与 distance之间的角度
     */
    private double angle;

    /**
     * 初始化
     */
    private boolean isInit = false;

    private Paint mCountPaint = null;
    private Paint mCirclePaint = null;
    private Path mPath = null;

    private float width;
    private float height;
    private float oriCircleX;//原始圆X
    private float oriCircleY;//原始圆Y

    private float msgCircleX;//消息圆X
    private float msgCircleY;//消息圆Y

    private float oriRadius;//原始圆半径
    private float msgRadius;//消息圆半径

    private String msgCount = 123+"";//消息数量

    private float msgTextSize = 20;//消息文字大小
    private float msgHalfWidth = 0.0f;
    private float msgHalfHeight = 0.0f;

    public MessageCountView(Context context) {
        this(context, null);
    }

    public MessageCountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.RED);
//        mCirclePaint.setStyle(Paint.Style.STROKE);//空心
        mCirclePaint.setStyle(Paint.Style.FILL);//填充

        mCountPaint = new Paint();
        mCountPaint.setAntiAlias(true);
        mCountPaint.setColor(Color.WHITE);
        mCountPaint.setTextSize(msgTextSize);

        mPath = new Path();
        measureText();
    }

    /**
     * 测量文字的宽高
     */
    private void measureText(){
        if(!TextUtils.isEmpty(msgCount)){
            Rect textRect = new Rect();
            mCountPaint.getTextBounds(msgCount,0,msgCount.length(),textRect);
            msgHalfWidth = textRect.width()/2.0f;
            msgHalfHeight = textRect.height()/2.0f;
        }
    }

    public float getMsgTextSize() {
        return msgTextSize;
    }

    public void setMsgTextSize(float msgTextSize) {
        this.msgTextSize = msgTextSize;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
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

        if (!isInit) {
//            paddingLeft = getPaddingLeft();
//            paddingTop = getPaddingTop();
//            paddingRight = getPaddingRight();
//            paddingBottom = getPaddingBottom();
            //初始化宽高
            width = w;
            height = h;
//            realWidth = width - paddingLeft - paddingRight;
            //初始化圆心
            oriCircleX = width / 2.0f;
            oriCircleY = height / 2.0f;
            msgCircleX = oriCircleX;
            msgCircleY = oriCircleY;
            //初始化半径
            oriRadius = width / 3.0f;
            msgRadius = width / 2.0f;

            isInit = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (currentState){
            case CURRENT_DRAG_STATE_NORMAL:
                canvas.drawCircle(msgCircleX, msgCircleY, msgRadius, mCirclePaint);//画消息圆
                canvas.drawText(msgCount, msgCircleX - msgHalfWidth, msgCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            case CURRENT_DRAG_STATE_UP_BACK:
                mPath.reset();
                if (flag) {
                    mPath.moveTo((float) (oriCircleX - oriRadius * Math.sin(angle)), (float) (oriCircleY - oriRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f, (float) (msgCircleX - msgRadius * Math.sin(angle)), (float) (msgCircleY - msgRadius * Math.cos(angle)));
                    mPath.lineTo((float) (msgCircleX + msgRadius * Math.sin(angle)), (float) (msgCircleY + msgRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f,(float)(oriCircleX + oriRadius*Math.sin(angle)),(float)(oriCircleY + oriRadius*Math.cos(angle)));
                    mPath.close();
                    canvas.drawPath(mPath,mCirclePaint);
                }else{

                }
                canvas.drawCircle(oriCircleX, oriCircleY, oriRadius, mCirclePaint);//画原始的圆
                canvas.drawCircle(msgCircleX, msgCircleY, msgRadius, mCirclePaint);//画消息圆
                canvas.drawText(msgCount, msgCircleX - msgHalfWidth, msgCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            case CURRENT_DRAG_STATE_UP_BREAK:
                canvas.drawCircle(oriCircleX, oriCircleY, oriRadius, mCirclePaint);//画原始的圆
                canvas.drawCircle(msgCircleX, msgCircleY, msgRadius, mCirclePaint);//画消息圆
                canvas.drawText(msgCount, msgCircleX - msgHalfWidth, msgCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        msgCircleX = event.getX();
        msgCircleY = event.getY();
        Log.i(TAG, "msgCircleX=" + msgCircleX + ",msgCircleY" + msgCircleY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                updateState();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == CURRENT_DRAG_STATE_NORMAL || currentState == CURRENT_DRAG_STATE_UP_BACK)
                    backInit();
                else if (currentState == CURRENT_DRAG_STATE_UP_BREAK){
                    currentState = CURRENT_DRAG_STATE_BREAK;
                    invalidate();
                    //消息圆break
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 更新状态
     */
    private void updateState(){
        float dx = Math.abs(oriCircleX - msgCircleX);
        float dy = Math.abs(oriCircleY - msgCircleY);
        double dis = Math.sqrt(dx*dx + dy*dy);

        if (dis <= maxDistance){
            if (currentState == CURRENT_DRAG_STATE_UP_BREAK )
                currentState = CURRENT_DRAG_STATE_UP_BREAK;
            else if (currentState == CURRENT_DRAG_STATE_BREAK)
                currentState = CURRENT_DRAG_STATE_BREAK;
            else
                currentState = CURRENT_DRAG_STATE_UP_BACK;
        }else{
            currentState = CURRENT_DRAG_STATE_UP_BREAK;
        }
        Log.i(TAG,"currentState = "+currentState);

        flag = (msgCircleX - oriCircleX)*(msgCircleY - oriCircleY) <=0;
        angle = Math.atan(dy/dx);
    }

    private void backInit(){
        msgCircleY = oriCircleY;
        msgCircleX = oriCircleX;
        invalidate();
    }
}
