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
 * 类似于 qq消息个数提示，可以滑动消失<br/>
 * 参考来源：https://github.com/lovejjfg/Circle
 */
public class MessageCountView extends View {

    private static final String TAG = "MessageCountView";
    private static final int defaultWidth = 50;//默认宽度
    private static final int defaultHeight = 50;//默认高度

    /**
     * 可以正常拖拽的最大距离
     */
    private static final float maxDistance = 300;

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
     * 标记。
     * true 表示 msgCircle在以oriCircle为圆心的 第1，3象限
     * false 表示 msgCircle在以oriCircle为圆心的 第2,4象限
     */
    private boolean flag = false;
    /**
     * dx与 distance之间的夹角角度
     */
    private double angle;

    /**
     * 初始化
     */
    private boolean isInit = false;

    private Paint mCountPaint = null;//消息画笔
    private Paint mCirclePaint = null;//圆和贝塞尔曲线的画笔
    private Path mPath = null;

    private float oriCircleX;//原始位置圆X
    private float oriCircleY;//原始位置圆Y

    private float msgCircleX;//消息圆X
    private float msgCircleY;//消息圆Y

    private float oriRadius;//原始位置圆半径
    private float msgRadius;//消息圆半径

    private String msgCount = "";//消息数量

    private float msgTextSize = 20;//消息文字大小
    private float msgHalfWidth = 0.0f;//消息文字的宽度
    private float msgHalfHeight = 0.0f;//消息文字的高度

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
        measureText();
        //如果消息圆已经消失，再次有新的消息了，开始显示消息圆。
        if (currentState == CURRENT_DRAG_STATE_BREAK && !TextUtils.isEmpty(msgCount)){
            currentState = CURRENT_DRAG_STATE_NORMAL;
        }
        invalidate();
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
            //初始化圆心
            oriCircleX = w / 2.0f;
            oriCircleY = h / 2.0f;
            //初始化半径
            msgRadius = (w- getPaddingLeft() - getPaddingRight()) / 2.0f;
            oriRadius = msgRadius;
            isInit = true;
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(getMsgCount()))
            return;

        switch (currentState){
            case CURRENT_DRAG_STATE_NORMAL:
                canvas.drawCircle(oriCircleX, oriCircleY, msgRadius, mCirclePaint);//原是位置画消息圆
                canvas.drawText(msgCount, oriCircleX - msgHalfWidth, oriCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            case CURRENT_DRAG_STATE_UP_BACK:
                mPath.reset();
                if (flag) {//坐标系第一三象限
                    mPath.moveTo((float) (oriCircleX - oriRadius * Math.sin(angle)), (float) (oriCircleY - oriRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f, (float) (msgCircleX - msgRadius * Math.sin(angle)), (float) (msgCircleY - msgRadius * Math.cos(angle)));
                    mPath.lineTo((float) (msgCircleX + msgRadius * Math.sin(angle)), (float) (msgCircleY + msgRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f, (float) (oriCircleX + oriRadius * Math.sin(angle)), (float) (oriCircleY + oriRadius * Math.cos(angle)));
                }else{//坐标系第二四象限
                    mPath.moveTo((float) (oriCircleX + oriRadius * Math.sin(angle)), (float) (oriCircleY - oriRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f, (float) (msgCircleX + msgRadius * Math.sin(angle)), (float) (msgCircleY - msgRadius * Math.cos(angle)));
                    mPath.lineTo((float) (msgCircleX - msgRadius * Math.sin(angle)), (float) (msgCircleY + msgRadius * Math.cos(angle)));
                    mPath.quadTo((oriCircleX + msgCircleX) * 0.5f, (oriCircleY + msgCircleY) * 0.5f, (float) (oriCircleX - oriRadius * Math.sin(angle)), (float) (oriCircleY + oriRadius * Math.cos(angle)));
                }
                mPath.close();
                canvas.drawPath(mPath,mCirclePaint);
                canvas.drawCircle(oriCircleX, oriCircleY, oriRadius, mCirclePaint);//画原始位置的圆
                canvas.drawCircle(msgCircleX, msgCircleY, msgRadius, mCirclePaint);//画消息圆
                canvas.drawText(msgCount, msgCircleX - msgHalfWidth, msgCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            case CURRENT_DRAG_STATE_UP_BREAK:
                canvas.drawCircle(msgCircleX, msgCircleY, msgRadius, mCirclePaint);//画消息圆
                canvas.drawText(msgCount, msgCircleX - msgHalfWidth, msgCircleY + msgHalfHeight, mCountPaint);//画文字
                break;
            case CURRENT_DRAG_STATE_BREAK:
                setMsgCount("");
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
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            case MotionEvent.ACTION_MOVE:
                updateState();
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == CURRENT_DRAG_STATE_NORMAL || currentState == CURRENT_DRAG_STATE_UP_BACK){
                    currentState = CURRENT_DRAG_STATE_NORMAL;
                }else if (currentState == CURRENT_DRAG_STATE_UP_BREAK){
                    currentState = CURRENT_DRAG_STATE_BREAK;
                }
                invalidate();
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

            oriRadius = msgRadius*((float)(maxDistance - dis)/maxDistance);//原始位置圆的半径随着dis的增加而减小
        }else{
            currentState = CURRENT_DRAG_STATE_UP_BREAK;
            oriRadius = msgRadius;//恢复原始位置圆的半径
        }
        Log.i(TAG,"currentState = "+currentState);

        flag = (msgCircleX - oriCircleX)*(msgCircleY - oriCircleY) <=0;
        angle = Math.atan(dy/dx);
        invalidate();
    }
}
