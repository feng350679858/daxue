package com.jingcai.apps.aizhuan.util;

import android.content.Context;

/**
 * Created by Json Ding on 2015/5/11.
 */
public class PixelUtil {
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
