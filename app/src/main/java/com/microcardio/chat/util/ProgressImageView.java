package com.microcardio.chat.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by AMOBBS on 2016/12/6.
 */
public class ProgressImageView extends ImageView{
    private Context mContext;
    public static final int STROKE_WIDTH = 5;
    private int mStrokeWidth;
    private Paint mPaint;
    private int mCenterX;
    private int mCenterY;
    private boolean mShowProgress = false;// 是否显示
    private int mRadius = 20;// 圈圈半径
    private int currentPosition = 0;// 当前开始画的位置
    private int mCurrentCount = 5;// 转圈圈的块块的个数

    public ProgressImageView(Context context) {
        super(context);
        mContext = context;
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private void init() {
        float scale = mContext.getResources().getDisplayMetrics().density;
        mStrokeWidth = (int) (STROKE_WIDTH * scale);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 消除锯齿


        mCenterX = getWidth() / 2;// 圆心x
        mCenterY = getHeight() / 2;// 圆心y
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowProgress) {
            if (mCenterX == 0 || mCenterY == 0) {
                init();
            }
            // 画背景
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            //mPaint.setColor(Color.parseColor("#70000000"));// 半透明
            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
            // 画块块
            drawOval(canvas, mCenterX, mRadius);
        }
    }

    public void startProgress() {
        mShowProgress = true;
    }

    public void stopProgress() {
        mShowProgress = false;
    }

    /**
     * 根据参数画出每个小块
     *
     * @param canvas
     * @param centre
     * @param radius
     */
    private void drawOval(Canvas canvas, int centre, int radius) {
        int mCount;// 块块的个数
        float mSplitSize = 8 * 1.0f;// 块块空隙的宽度
        float itemSize = 8 * 1.0f;// 每个块块的宽度

        // 根据块块的大小和空隙的大小求出块块的个数
        mCount = (int) (360 * 1.0f / (mSplitSize + itemSize));
        // 求余数，将余数分配到空隙
        float yu = (360 * 1.0f % (mSplitSize + itemSize));
        mSplitSize += yu / mCount;

        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.parseColor("#B5B5B5")); // 设置圆环的颜色

        for (int i = 0; i < mCount; i++) {
            canvas.drawArc(oval, i * (itemSize + mSplitSize), itemSize, false,
                    mPaint); // 根据进度画圆弧
        }
        if (currentPosition == mCount - 1) {
            currentPosition = 0;
        }

        for (int i = 0; i < mCurrentCount; i++) {
            if (i==mCurrentCount-2) {
                mPaint.setColor(Color.parseColor("#70575757")); // 设置圆环的颜色
            }else if(i==mCurrentCount-1) {
                mPaint.setColor(Color.parseColor("#b4575757")); // 设置圆环的颜色
            }else{
                mPaint.setColor(Color.parseColor("#50575757")); // 设置圆环的颜色
            }
            canvas.drawArc(oval, (i + currentPosition)
                    * (itemSize + mSplitSize), itemSize, false, mPaint); // 根据进度画圆弧
        }
        currentPosition++;
        invalidate();
    }
}
