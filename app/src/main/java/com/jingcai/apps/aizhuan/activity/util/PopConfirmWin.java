package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.PopupDialog;

/**
 * Created by lejing on 15/8/15.
 */
public class PopConfirmWin {
    private final Activity activity;
    private PopupDialog win;

    public PopConfirmWin(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        //显示立即帮助确认对话框
        int screen_width = 0;
        if (activity instanceof BaseActivity) {
            screen_width = ((BaseActivity) activity).getScreenWidth();
        } else {
            Point point = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(point);
            screen_width = point.x;
        }
        win = PopupDialog.Builder.create(activity)
                .setWidth((int) (screen_width * 0.8))
                .setAnimstyle(R.anim.dialog_appear)
                .setContentViewLayout(R.layout.pop_confirm)
                .build();

        setCancelAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                win.dismiss();
            }
        });
    }


    public PopConfirmWin setTitle(String txt) {
        if (null == txt || txt.length() < 1) {
            win.findTextViewById(R.id.tv_title).setVisibility(View.GONE);
        }
        win.findTextViewById(R.id.tv_title).setText(txt);
        return this;
    }

    public PopConfirmWin setContent(String txt) {
        win.findTextViewById(R.id.tv_content).setText(txt);
        return this;
    }

    public PopConfirmWin setOkAction(View.OnClickListener listener) {
        return setOkAction("确定", listener);
    }

    public PopConfirmWin setOkAction(String txt, View.OnClickListener listener) {
        Button btn = win.findButtonById(R.id.btn_confirm);
        if (null == listener) {
            btn.setVisibility(View.GONE);
            return this;
        }
        btn.setText(txt);
        btn.setOnClickListener(listener);
        return this;
    }

    public PopConfirmWin setCancelAction(View.OnClickListener listener) {
        return setCancelAction("取消", listener);
    }

    public PopConfirmWin setCancelAction(String txt, View.OnClickListener listener) {
        Button btn = win.findButtonById(R.id.btn_cancel);
        if (null == listener) {
            btn.setVisibility(View.GONE);
            return this;
        }
        btn.setText(txt);
        btn.setOnClickListener(listener);
        return this;
    }

    public boolean inShowing(){
        return win.isShowing();
    }

    public void show() {
        win.show();
    }

    public void dismiss() {
        win.dismiss();
    }
}
