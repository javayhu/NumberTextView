package me.javayhu.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * 自带数字切换效果的数字显示组件
 * <p>
 * Created by javayhu on 10/24/17.
 */
public class NumberTextView extends View {

    private static final String TAG = NumberTextView.class.getSimpleName();

    private static final int DEFAULT_COUNT = 0;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_SIZE = 70;//font px size
    private static final int DEFAULT_DURATION = 400;//android.R.integer.config_mediumAnimTime

    private int mCount;//当前的数值
    private int mTextColor;//数字的颜色
    private int mTextSize;//数字的大小
    private int mDuration;//动画的执行时间

    //以 123 和 124 为例，mCountSamePart 是 12， mCountDiffOld 是 3， mCountDiffNew 是 4
    private String mCountSamePart;
    private String mCountDiffOld;
    private String mCountDiffNew;

    private Paint mTextPaint;
    private ValueAnimator mValueAnimator;
    private int mState = 0;//1表示增大，-1表示减小

    public NumberTextView(Context context) {
        this(context, null);
    }

    public NumberTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.NumberTextViewStyle);
    }

    public NumberTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberTextView, defStyleAttr, R.style.Widget_NumberTextViewStyle);
        this.mTextColor = attributes.getColor(R.styleable.NumberTextView_android_textColor, DEFAULT_COLOR);
        this.mTextSize = attributes.getDimensionPixelSize(R.styleable.NumberTextView_android_textSize, DEFAULT_SIZE);
        this.mCount = attributes.getInteger(R.styleable.NumberTextView_count, DEFAULT_COUNT);
        this.mDuration = attributes.getInteger(R.styleable.NumberTextView_duration, DEFAULT_DURATION);
        attributes.recycle();

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);//像素值

        resetCountParts();
        setClickable(true);
    }

    private void resetCountParts() {
        mCountSamePart = String.valueOf(mCount);
        mCountDiffOld = "";
        mCountDiffNew = "";
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(calculateWidth(widthMeasureSpec), calculateHeight(heightMeasureSpec));
    }

    private int calculateWidth(int widthMeasureSpec) {
        int width = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                width = size;
                break;
            case MeasureSpec.AT_MOST:
                width = getMinWidth();
                break;
            case MeasureSpec.UNSPECIFIED:
                width = Math.max(getMinWidth(), size);
                break;
        }
        return width;
    }

    private int calculateHeight(int heightMeasureSpec) {
        int height = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        switch (mode) {
            case MeasureSpec.EXACTLY:
                height = size;
                break;
            case MeasureSpec.AT_MOST:
                height = getMinHeight();
                break;
            case MeasureSpec.UNSPECIFIED:
                height = Math.max(getMinHeight(), size);
                break;
        }
        return height;
    }

    private int getMinWidth() {
        int size = getPaddingLeft() + getPaddingRight();
        String maxLengthStr = mCountSamePart;
        if (mCountDiffNew != null && mCountDiffOld != null) {
            maxLengthStr += mCountDiffNew.length() > mCountDiffOld.length() ? mCountDiffNew : mCountDiffOld;
        }
        if (mTextPaint != null && !TextUtils.isEmpty(String.valueOf(maxLengthStr))) {
            size += (int) mTextPaint.measureText(String.valueOf(maxLengthStr));
        }
        return size;
    }

    private int getMinHeight() {
        return getPaddingTop() + getPaddingBottom() + mTextSize;
    }

    public void plusOne() {
        setCount(mCount + 1);
        mState = 1;

        startAnimation(0f, 1f);
    }

    public void minusOne() {
        if (mCount <= 0) {
            throw new IllegalStateException("Number can not be less than 0!");
        }

        setCount(mCount - 1);
        mState = -1;

        startAnimation(0f, -1f);
    }

    private void startAnimation(float start, float end) {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mValueAnimator = ValueAnimator.ofFloat(start, end);
        mValueAnimator.setDuration(mDuration);
        mValueAnimator.setInterpolator(new LinearInterpolator());//new OvershootInterpolator()
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mValueAnimator = null;
                //从100到99的时候需要重新计算下，因为之前得到的长度是[100]三位数字，动画结束之后只显示[99]两位数字，所以需要重新layout
                if (mCountDiffOld != null && mCountDiffNew != null && mCountDiffOld.length() != mCountDiffNew.length()) {
                    resetCountParts();
                    requestLayout();
                }
            }
        });
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int startX = 0;
        int startY = (int) (canvas.getHeight() / 2 + (Math.abs(mTextPaint.ascent()) - mTextPaint.descent()) / 2);

        int offsetSamePart = 0;
        if (!TextUtils.isEmpty(mCountSamePart)) {
            canvas.drawText(mCountSamePart, startX, startY, mTextPaint);
            offsetSamePart = (int) mTextPaint.measureText(mCountSamePart);
        }

        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            canvas.drawText(mCountDiffOld, startX + offsetSamePart, startY - mState * (int) (mValueAnimator.getAnimatedFraction() * mTextSize), mTextPaint);
            canvas.drawText(mCountDiffNew, startX + offsetSamePart, startY + mTextSize * mState - mState * (int) (mValueAnimator.getAnimatedFraction() * mTextSize), mTextPaint);
        } else {
            canvas.drawText(mCountDiffNew, startX + offsetSamePart, startY, mTextPaint);
        }
    }

    public void refresh(int oldCount, int newCount) {
        String strOldCount = String.valueOf(oldCount);
        String strNewCount = String.valueOf(newCount);

        if (oldCount == newCount) {
            mCountSamePart = strNewCount;
            mCountDiffOld = "";
            mCountDiffNew = "";
            return;
        }

        if (strOldCount.length() != strNewCount.length()) {
            mCountSamePart = "";
            mCountDiffOld = strOldCount;
            mCountDiffNew = strNewCount;
            requestLayout();//数字位数不同的话需要重新measure
        } else {
            for (int i = 0; i < strOldCount.length(); i++) {
                if (strOldCount.charAt(i) != strNewCount.charAt(i)) {
                    mCountSamePart = i == 0 ? "" : strOldCount.substring(0, i);
                    mCountDiffOld = strOldCount.substring(i);
                    mCountDiffNew = strNewCount.substring(i);
                    break;
                }
            }
            postInvalidate();//数字位数相同重绘即可
        }
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        int oldCount = mCount;
        this.mCount = count;
        refresh(oldCount, mCount);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        this.mTextColor = color;
        if (mTextPaint != null) {
            mTextPaint.setColor(color);
        }
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int size) {
        this.mTextSize = size;
        if (mTextPaint != null) {
            mTextPaint.setTextSize(size);
        }
        invalidate();
    }

}
