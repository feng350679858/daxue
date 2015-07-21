package com.jingcai.apps.aizhuan.view;

/**
 * Created by Administrator on 2015/7/15.
 */
public interface OnToggleStateChangeListener {
        /**
         * 当开关状态改变回调此方法
         *
         * @param b 当前开关的最新状态
         */
        void onToggleStateChange(final boolean b);
}