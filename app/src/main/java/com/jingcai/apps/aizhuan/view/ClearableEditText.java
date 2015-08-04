package com.jingcai.apps.aizhuan.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.jingcai.apps.aizhuan.R;


/**
 * Created by Json Ding on 2015/4/25.
 */
public class ClearableEditText extends EditText implements View.OnFocusChangeListener {

    private Drawable mDrawableRight;
    private Drawable mDrawableLeft;
    private Drawable mDrawableUp;
    private Drawable mDrawableDown;
    private Rect mBounds;
    private Context mContext;

    public ClearableEditText(Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        //删除按钮的图片资源
        setOnFocusChangeListener(this);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        Drawable[] drawables = this.getCompoundDrawables();
        mDrawableLeft = drawables[0];
        mDrawableUp = drawables[1];
        mDrawableRight = drawables[2];
        mDrawableDown = drawables[3];

        setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableUp, null, mDrawableDown);
        mDrawableRight = (mDrawableRight==null?context.getResources().getDrawable(R.drawable.widget_clearable_edittext_del):mDrawableRight);
    }

    private void setDrawable() {
        if (length() == 0 ) {
            setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableUp, null, mDrawableDown);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableUp, mDrawableRight, mDrawableDown);
        }
    }


    /**
     * event.getX() 获取相对应自身左上角的X坐标
     * event.getY() 获取相对应自身左上角的Y坐标
     * getWidth() 获取控件的宽度
     * getTotalPaddingRight() 获取删除图标左边缘到控件右边缘的距离
     * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离
     * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
     * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawableRight != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            //判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
                    (x < (getWidth() - getPaddingRight()));
            //获取删除图标的边界，返回一个Rect对象
            Rect rect = mDrawableRight.getBounds();
            //获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            //计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;
            //判断触摸点是否在竖直范围内(可能会有点误差)
            //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerWidth && isInnerHeight) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mDrawableRight = null;
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        setOnFocusChangeListener(this);
//    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            setDrawable();
        }else {
            setCompoundDrawablesWithIntrinsicBounds(mDrawableLeft, mDrawableUp, null, mDrawableDown);
        }
        invalidate();
    }
}