package com.jingcai.apps.aizhuan.activity.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.mine.SafeCheckActivity;
import com.jingcai.apps.aizhuan.util.PopupWin;

/**
 * 设置支付密码框
 * 支付密码框
 * Created by lejing on 15/7/21.
 */
public class PayPwdWin {
    private static final String TAG = "PayPwdWin";
    private int[] inputTextIdArr = new int[]{R.id.tv_psw_length_1, R.id.tv_psw_length_2, R.id.tv_psw_length_3, R.id.tv_psw_length_4, R.id.tv_psw_length_5, R.id.tv_psw_length_6};
    private View[] inputTextViewArr = new View[6];
    private Activity baseActivity;
    private StringBuilder mPayPsw = new StringBuilder();
    private PopupWin mPopWin;
    private Callback callback;

    public PayPwdWin(Activity baseActivity) {
        this.baseActivity = baseActivity;
        init();
    }

    private void init() {
        View contentView = View.inflate(baseActivity, R.layout.pop_pay_psw, null);
        View parentView = baseActivity.getWindow().getDecorView();

        mPopWin = PopupWin.Builder.create(baseActivity)
                .setParentView(parentView)
                .setContentView(contentView)
//                .setFocusable(false)
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

        int i = 0;
        for (int id : inputTextIdArr) {
            inputTextViewArr[i++] = mPopWin.findViewById(id);
        }

        mPopWin.findViewById(R.id.tv_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, SafeCheckActivity.class);
                baseActivity.startActivity(intent);
            }
        });
    }

    public void setTitle(String title) {
        ((TextView) mPopWin.findViewById(R.id.tv_main_tip)).setText(title);
    }

    public void show() {
        setPwdOnly(true);
        clearAll();
        mPopWin.show();
    }

    public void showPay(double money) {
        setPwdOnly(false);
        ((TextView) mPopWin.findViewById(R.id.tv_pay_num)).setText(String.format("%.2f元",money));
        clearAll();
        mPopWin.show();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void setPwdOnly(boolean pwdOnly) {
        if (pwdOnly) {
            mPopWin.findViewById(R.id.layout_pay_num).setVisibility(View.GONE);
            mPopWin.findViewById(R.id.tv_forget).setVisibility(View.GONE);
        } else {
            mPopWin.findViewById(R.id.tv_set_pwd_tip).setVisibility(View.GONE);
        }
    }

    public interface Callback {
        void call(String pwd);
    }

    private void clearAll(){
        mPayPsw.delete(0, mPayPsw.length());
        for (View view : inputTextViewArr) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener padListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id != R.id.iv_back) {//数字
                if (!(v instanceof TextView)) {
                    return;
                }
                TextView tvNum = (TextView) v;
                if (mPayPsw.length() <= 6) {
                    mPayPsw.append(tvNum.getText());
                }
            } else {//后退键
                if (mPayPsw.length() <= 0) return;
                mPayPsw.deleteCharAt(mPayPsw.length() - 1);
            }
            for (int i = 0; i < inputTextIdArr.length; i++) {
                inputTextViewArr[i].setVisibility(i < mPayPsw.length() ? View.VISIBLE : View.INVISIBLE);
            }
            if (mPayPsw.length() >= inputTextIdArr.length) {
                mPopWin.dismiss();
                if (null != callback) {
                    callback.call(mPayPsw.toString().trim());
                }
            }

        }
    };

    public boolean isShowing() {
        return mPopWin.isShowing();
    }

    public void dismiss() {
        mPopWin.dismiss();
    }
}
