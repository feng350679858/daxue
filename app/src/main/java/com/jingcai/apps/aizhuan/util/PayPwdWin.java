package com.jingcai.apps.aizhuan.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;

/**
 * Created by lejing on 15/7/21.
 */
public class PayPwdWin {
    private static final String TAG = "PayPwdWin";
    private Activity baseActivity;
    private StringBuilder mPayPsw = new StringBuilder();
    private PopupWin mPopWin;
    private Callback callback;

    public PayPwdWin(Activity baseActivity) {
        this.baseActivity = baseActivity;
        init();
    }

    private void init() {
        View mPopupPayPswContentView = View.inflate(baseActivity, R.layout.pop_pay_psw, null);
        View parentView = baseActivity.getWindow().getDecorView();

        mPopWin = PopupWin.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(mPopupPayPswContentView)
                .build();
        mPopWin.setAction(R.id.iv_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWin.dismiss();
            }
        });

        mPopWin.findViewById(R.id.tv_num_0).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_1).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_2).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_3).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_4).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_5).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_6).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_7).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_8).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.tv_num_9).setOnClickListener(padListener);
        mPopWin.findViewById(R.id.iv_back).setOnClickListener(padListener);
    }

    public void setTitle(String title){
        ((TextView)mPopWin.findViewById(R.id.tv_title)).setText(title);
    }

    public void show() {
        mPopWin.show();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void finishInput(String pwd);

    }

    private View.OnClickListener padListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if (id != R.id.iv_back) {
                //数字
                if (!(v instanceof TextView)) {
                    return;
                }
                TextView tvNum = (TextView) v;
                if (mPayPsw.length() <= 6) {
                    mPayPsw.append(tvNum.getText());
                }
            } else {
                //后退键
                if (mPayPsw.length() <= 0) return;
                mPayPsw.deleteCharAt(mPayPsw.length() - 1);
            }

            for (int i = 1; i <= 6; i++) {
                try {
                    int viewId = baseActivity.getResources().getIdentifier("tv_psw_length_" + i, "id", baseActivity.getPackageName());
                    mPopWin.findViewById(viewId).setVisibility(i <= mPayPsw.length() ? View.VISIBLE : View.INVISIBLE);
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't find tv_psw_length_" + i + " ,Did you remove it?");
                }
            }
            if (mPayPsw.length() == 6) {

                mPopWin.dismiss();
                if (null != callback) {
                    callback.finishInput(mPayPsw.toString().trim());
                }
            }

        }
    };
}
