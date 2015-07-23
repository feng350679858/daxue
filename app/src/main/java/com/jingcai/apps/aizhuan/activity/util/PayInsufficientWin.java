package com.jingcai.apps.aizhuan.activity.util;

import android.view.Gravity;
import android.view.View;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/7/22.
 */
public class PayInsufficientWin {

    private final BaseActivity baseActivity;
    private PopupWin mPopWin;

    public PayInsufficientWin(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        init();
    }

    public void show() {
        mPopWin.show(Gravity.CENTER, 0, 0);
    }

    private void init() {
        View contentView = View.inflate(baseActivity, R.layout.pop_pay_insufficient, null);
        View parentView = baseActivity.getWindow().getDecorView();

        int w = baseActivity.getScreenWidth() * 80 / 100;
        mPopWin = PopupWin.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(contentView)
                .setAnimstyle(0)
                .setFocusable(false)
                .setWidth(w)
                .build();
        mPopWin.setAction(R.id.tv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
            }
        });
        mPopWin.setAction(R.id.tv_go, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                baseActivity.showToast("跳转至充值");
            }
        });
    }

    public boolean isShowing() {
        return mPopWin.isShowing();
    }

    public void dismiss() {
        mPopWin.dismiss();
    }
}
