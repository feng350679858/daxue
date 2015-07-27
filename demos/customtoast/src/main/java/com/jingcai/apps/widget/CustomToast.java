package com.jingcai.apps.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.jingcai.apps.custometoast.R;

import java.util.Timer;
import java.util.TimerTask;

public class CustomToast {

    private WindowManager wdm;
    private double time;
    private View mView;
    private WindowManager.LayoutParams params;
    private Timer timer;
    private Activity mActivity;
    private boolean mIsShowing;

    private CustomToast(Context context, String text, double time) {
        wdm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        timer = new Timer();
        mActivity = (Activity) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mView = inflater.inflate(R.layout.toast_layout, null);

        ((TextView) mView.findViewById(R.id.tv_text)).setText(text);
        final Point outSize = new Point();
        wdm.getDefaultDisplay().getSize(outSize);

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = outSize.x;
        params.format = PixelFormat.TRANSLUCENT;

        params.windowAnimations = R.style.toastInAndOutAnim;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        params.gravity = Gravity.TOP;
        params.y = 0;
        params.x = 0;

        this.time = time;
    }

    public void setGravity(int gravity){
        params.gravity = gravity;
    }

    public void setOffsetY(int offsetY){
        params.y = offsetY;
    }

    public static CustomToast makeText(Context context, String text, double time) {
        CustomToast customeToast = new CustomToast(context, text, time);
        return customeToast;
    }


    public void show() {
        try {
            if (mIsShowing) {
                timer.cancel();
                handleHide();
            }
            handleShow();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleHide();
                        }
                    });
                }
            }, (long) (time * 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleShow() {
        if (mView.getParent() != null) {
            wdm.removeView(mView);
        }
        wdm.addView(mView, params);
        timer = new Timer();
        mIsShowing = true;
    }

    private void handleHide() {
        wdm.removeViewImmediate(mView);
        mIsShowing = false;
        timer = null;
    }
}