package com.zhg.views.progresstextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by User on 2017/4/7.
 */

public class ProgressTextView extends View {
    //文本在整个视图中X坐标上的起始位置以及在Y坐标上的起始位置
    private int mTextStartOffsetX,mTextStartOffsetY;

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_TOP = 2;
    private static final int DIRECTION_BOTTOM = 3;

    //这个就是最终要绘制出来的文本
    private String mText = "ABCDEFG";
    //文本绘制的画笔
    private Paint mPaint;
    //文本字体的大小
    private int mFontSize = sp2px(30);
    //原始文本颜色
    private int mFontOriginColor = 0xff000000;
    //逐变文本的颜色
    private int mFontChangeColor = 0xffff0000;
    //用来确定changeColor颜色的绘制方向(文本绘制)
    private int mDirection = DIRECTION_LEFT;
    //用来记录文本的宽高
    private int mTextWidth,mTextHeight;
    //逐变的一个渐变范围
    private float mRange;


    public ProgressTextView(Context context, int mTextStartOffsetX) {
        super(context,null);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//设置抗锯齿
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.ProgressTextView);
        mText = ta.getString(R.styleable.ProgressTextView_text);
        mFontSize = ta.getDimensionPixelSize(R.styleable.ProgressTextView_fontSize,mFontSize);
        mFontOriginColor  = ta.getColor(R.styleable.ProgressTextView_fontOriginColor,mFontOriginColor);
        mFontChangeColor = ta.getColor(R.styleable.ProgressTextView_fontChangeColor,mFontChangeColor);
        mRange = ta.getFloat(R.styleable.ProgressTextView_range,mRange);
        mDirection = ta.getInt(R.styleable.ProgressTextView_direction,mDirection);
        ta.recycle();
        mPaint.setTextSize(mFontSize);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureText();//测量文本的宽高
        int width  = measureWidth(widthMeasureSpec);//测量整个View的宽
        int height = measureHeight(heightMeasureSpec);//测量整个View的高
        setMeasuredDimension(width,height);//确认整个View最终宽高
        //确定文本在View中绘制位置X，默认横向剧中
        mTextStartOffsetX = getMeasuredWidth() / 2 - mTextWidth / 2 ;
        //确定文本在View中绘制位置，默认纵向剧中
        mTextStartOffsetY = (int) (getMeasuredHeight() / 2 - mTextHeight / 2- mPaint.ascent());
    }


    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int value = MeasureSpec.getSize(heightMeasureSpec);

        int height = 0;
        switch (heightMode){
            case MeasureSpec.EXACTLY://精确值或者MATCH_PARENT
                height = value;
                break;
            case MeasureSpec.AT_MOST://WRAP_PARENT
            case MeasureSpec.UNSPECIFIED:
                height = mTextHeight + getPaddingTop()+getPaddingBottom();
                break;
        }
        height = heightMode == MeasureSpec.AT_MOST ? Math.min(height,value) : height;
        return height;
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int value = MeasureSpec.getSize(widthMeasureSpec);
        int width = 0;
        switch (widthMode){
            case MeasureSpec.EXACTLY://精确值或者MATCH_PARENT
                width = value;
                break;
            case MeasureSpec.AT_MOST://WRAP_PARENT
            case MeasureSpec.UNSPECIFIED:
                width = mTextWidth + getPaddingLeft()+getPaddingRight();
                break;
        }
        width = widthMode == MeasureSpec.AT_MOST ? Math.min(width,value) : width;
        return width;
    }

    private void measureText() {
        mTextWidth = (int) mPaint.measureText(mText);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.descent - fm.ascent);
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());
    }

    public void reverseColor() {
        int tmp = mFontOriginColor;
        mFontOriginColor = mFontChangeColor;
        mFontChangeColor = tmp;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mDirection){
            case DIRECTION_BOTTOM:
                break;
            case DIRECTION_LEFT:
                drawTextFromLeft(canvas);
                break;
            case DIRECTION_RIGHT:
                drawTextFromRight(canvas);
                break;
            case DIRECTION_TOP:
                break;
        }
    }

    private void drawTextFromRight(Canvas canvas) {
        mPaint.setColor(mFontChangeColor);

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mTextStartOffsetX+(1-mRange)*mTextWidth,0,(mTextStartOffsetX+mTextWidth),getMeasuredHeight());
        canvas.drawText(mText,mTextStartOffsetX,mTextStartOffsetY,mPaint);
        canvas.restore();

        mPaint.setColor(mFontOriginColor);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(mTextStartOffsetX,0,mTextStartOffsetX+(1-mRange)*mTextWidth,getMeasuredHeight());
        canvas.drawText(mText,mTextStartOffsetX,mTextStartOffsetY,mPaint);
        canvas.restore();
    }

    private void drawTextFromLeft(Canvas canvas) {
        int range = (int) (mRange * mTextWidth + mTextStartOffsetX);
        mPaint.setColor(mFontChangeColor);//设置画笔的颜色为渐变色
        canvas.save();//保存当前canvas的状态(大小、偏移等状态)
        //剪切锁定一个矩形，只有这个矩形中文本需要改变颜色，从而不影响其他区域的，其他区域不受影响
        canvas.clipRect(mTextStartOffsetX,0,range,getMeasuredHeight());
        //绘制文本，因为我们剪切锁定出来了一个举行，只有这个矩形中的文本颜色发生改变
        canvas.drawText(mText,mTextStartOffsetX,mTextStartOffsetY,mPaint);
        //绘制完毕并在屏幕显示(绘制切已经显示出来的效果是不可逆的)之后，还原到开始的canvas状态(即从刚刚剪切锁定的局部矩形切换到上一个save时候的状态大小)
        canvas.restore();

        //我们看到的效果是渐变色在逐渐增加，而原始色在逐渐减少，所以我们上面的渐变色画完之后，要立即画原始色的文本，下面的逻辑步骤和上面一致。
        mPaint.setColor(mFontOriginColor);//设置画笔颜色为静止原始色
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(range,0,mTextStartOffsetX+mTextWidth,getMeasuredHeight());
        canvas.drawText(mText,mTextStartOffsetX,mTextStartOffsetY,mPaint);
        canvas.restore();
    }

    public void setRange(float range) {
        this.mRange = range;
        invalidate();
    }


    public int getDirection() {
        return mDirection;
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }
}
