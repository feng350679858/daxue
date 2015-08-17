package com.jingcai.apps.aizhuan.activity.util;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineGoldRechargeActivity;
import com.jingcai.apps.aizhuan.util.PopupDialog2;

/**
 * Created by lejing on 15/7/22.
 */
public class PayInsufficientWin2 {

    private final BaseActivity baseActivity;
    private PopupDialog2 mPopWin;

    public PayInsufficientWin2(BaseActivity baseActivity) {
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
        mPopWin = PopupDialog2.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(contentView)
                .setAnimstyle(0)
//                .setFocusable(false)
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
                baseActivity.startActivity(new Intent(baseActivity, MineGoldRechargeActivity.class));
                mPopWin.dismiss();
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