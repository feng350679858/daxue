package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/8/15.
 */
public class PopConfirmWin {
    private final Activity activity;
    private PopupWin win;

    public PopConfirmWin(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        //显示立即帮助确认对话框
        View parentView = activity.getWindow().getDecorView();
        int screen_width = 0;
        if (activity instanceof BaseActivity) {
            screen_width = ((BaseActivity) activity).getScreenWidth();
        } else {
            Point point = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(point);
            screen_width = point.x;
        }
        win = PopupWin.Builder.create(activity)
                .setWidth((int) (screen_width * 0.8))
                .setAnimstyle(R.anim.dialog_appear)
                .setContentViewLayout(R.layout.pop_confirm)
                .setParentView(parentView)
                .build();
        win.findTextViewById(R.id.tv_title).setText("确认？");
        win.findTextViewById(R.id.tv_content).setText("确认立即帮助？");

        win.setAction(R.id.tv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
            }
        });
    }


    public PopConfirmWin setTitle(String txt) {
        win.findTextViewById(R.id.tv_title).setText(txt);
        return this;
    }

    public PopConfirmWin setContent(String txt) {
        win.findTextViewById(R.id.tv_content).setText(txt);
        return this;
    }

    public PopConfirmWin setAction(int resid, View.OnClickListener listener) {
        win.setAction(resid, listener);
        return this;
    }

    public void show() {
        win.show(Gravity.CENTER, 0, 0);
    }

    public void dismiss() {
        win.dismiss();
    }
}
