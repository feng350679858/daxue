package com.jingcai.apps.aizhuan.activity.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

/**
 * Created by lejing on 15/7/30.
 */
public class LevelTextView extends TextView {
    public LevelTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LevelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LevelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LevelTextView, defStyleAttr, 0);
        int level = a.getInt(R.styleable.LevelTextView_level, 0);
        a.recycle();

        if (0 != level) {
            setText("V" + level);
            setBackgroundResource(getColor(level));
        }
//        int width = (int)(context.getResources().getDimensionPixelSize(R.dimen.dp_10)*2.6f);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
//        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//        setLayoutParams(params);

        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        setTextColor(context.getResources().getColorStateList(R.color.white));
    }

    public void setLevel(int level) {
        setText("V" + String.valueOf(level));
        setBackgroundResource(getColor(level));
    }

    private int getColor(int level) {
        if (level >= 1 && level <= 5) {
            return R.drawable.level_bg_1_5;
        } else if (level >= 6 && level <= 10) {
            return R.drawable.level_bg_6_10;
        } else if (level >= 11 && level <= 15) {
            return R.drawable.level_bg_11_15;
        } else if (level >= 16) {
            return R.drawable.level_bg_16_20;
        }
        return 0;
    }
}
