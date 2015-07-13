package com.jingcai.apps.aizhuan.activity.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.util.InnerLock;

import java.util.Stack;

/**
 * Created by lejing on 15/4/28.
 */
public class BaseFragment extends Fragment {
    protected Activity baseActivity;
    protected Looper looper = null;
    private ProgressDialog progressDialog =null;
    private final int REQUEST_CODE_LOGIN = 101;
    protected static int screen_height, screen_width;
    protected static float density;
    protected final InnerLock actionLock = new InnerLock();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseActivity = getActivity();

        density = baseActivity.getApplicationContext().getResources().getDisplayMetrics().density;
        Point point = new Point();
        baseActivity.getWindowManager().getDefaultDisplay().getSize(point);
        screen_width = point.x;
        screen_height = point.y;

        looper = Looper.myLooper();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 检查是否登录
     *
     * @return true 已登录
     */
    protected boolean checkLogin() {
        return UserSubject.isLogin();
    }

    /**
     * 检查是否登录，如果未登录，则跳转到登录页面
     *
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
        //TODO 更换为新的LoginActivity
//        Intent intent = new Intent(getActivity(), LoginActivity.class);
//        startActivityForResult(intent, REQUEST_CODE_LOGIN);
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

    public void showProgressDialog(String msg) {
//        if(null == progressDialog) {
//            Log.d("==", "create dialog");
//            progressDialog = new ProgressDialog(LoginActivity.this);
//        }
//        progressDialog.setMessage(msg);
//        progressDialog.show();
        progressDialog = ProgressDialog.show(getActivity(), null, msg, false, false);
//        progressDialog = ProgressDialog.show(this, "提示", msg, false, false);
    }

    public void closeProcessDialog(){
        if(null != progressDialog) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private static Stack<BaseFragment> fragmentStack = new Stack<>();

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Fragment parentFragment = getParentFragment();
        if(null != parentFragment){
            fragmentStack.push(this);
            parentFragment.startActivityForResult(intent, requestCode);
        }else {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BaseFragment fragment = fragmentStack.isEmpty()?null:fragmentStack.pop();
        if(null != fragment){
            fragment.onActivityResult(requestCode, resultCode, data);
            return ;
        }
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

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(this.getClass().getName()); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(this.getClass().getName());
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
     * 返回键，需要配合activity
     * @return
     */
    public boolean keyBack(){
        return false;
    }
}
