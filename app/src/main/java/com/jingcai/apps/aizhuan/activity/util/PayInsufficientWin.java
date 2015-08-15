package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.base.BaseFragmentActivity;
import com.jingcai.apps.aizhuan.activity.mine.gold.MineGoldRechargeActivity;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * Created by lejing on 15/7/22.
 */
public class PayInsufficientWin {

    private final Activity baseActivity;
    private PopupWin mPopWin;

    public PayInsufficientWin(Activity baseActivity) {
        this.baseActivity = baseActivity;
        init();
    }

    public void show() {
        mPopWin.show(Gravity.CENTER, 0, 0);
    }

    private void init() {
        View contentView = View.inflate(baseActivity, R.layout.pop_pay_insufficient, null);
        View parentView = baseActivity.getWindow().getDecorView();

        int w = 0;
        if(baseActivity instanceof BaseActivity){
            w = ((BaseActivity)baseActivity).getScreenWidth() * 80 / 100;
        }else {
            Point point = new Point();
            baseActivity.getWindowManager().getDefaultDisplay().getSize(point);
            w = point.x * 80 / 100;
        }

        mPopWin = PopupWin.Builder.create(baseActivity)
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
