package com.jingcai.apps.aizhuan.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.util.InnerLock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lejing on 15/4/25.
 */
public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private final int REQUEST_CODE_LOGIN = 101;
    protected static int screen_height, screen_width;
    protected static float density;
    protected Looper looper = null;
    private ProgressDialog progressDialog = null;
    protected final InnerLock actionLock = new InnerLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        screen_width = point.x;
        screen_height = point.y;
        density = getApplicationContext().getResources().getDisplayMetrics().density;

        looper = Looper.myLooper();
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String msg) {
        progressDialog = ProgressDialog.show(this, null, msg, false, false);
    }

    public void closeProcessDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    /**
     * 在对应控件上，显示输入法
     *
     * @param view
     */
    protected void showInputMethodDialog(final View view) {
        view.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) BaseActivity.this.getSystemService(INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.showSoftInput(view, 0);
//                inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    /**
     * 隐藏输入发
     */
    public static void hideInputMethodDialog(Context context) {
        View view = ((Activity) context).getWindow().peekDecorView();
        if (view != null && view.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 检查是否登录
     * @return true 已登录
     */
    protected boolean checkLogin() {
        return UserSubject.isLogin();
    }

    /**
     * 检查是否登录，如果未登录，则跳转到登录页面
     * @return true 已登录
     */
    protected boolean checkAndPerformLogin() {
        if (UserSubject.isLogin()) {
            return true;
        }
        startActivityForLogin();
        return false;
    }

    /**
     * 跳转到登录页面
     */
    protected void startActivityForLogin() {
        //TODO 需要跳转到新的登录页面
 //       Intent intent = new Intent(this, LoginActivity.class);
  //     startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    /**
     * 登录后回调方法
     */
    protected void afterLoginSuccess() {
    }

    /**
     * 登录失败后回调方法
     */
    protected void afterLoginFail() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOGIN: {
                if (resultCode == Activity.RESULT_OK) {
                    afterLoginSuccess();
                    break;
                } else {
                    afterLoginFail();
                    break;
                }
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);//友盟sdk
//        JPushInterface.onResume(this);
    }

    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);//友盟sdk
//        JPushInterface.onPause(this);
    }
}