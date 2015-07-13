package com.jingcai.apps.aizhuan.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.entity.BadgeBean;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.UserSubject;

/**
 * Created by lejing on 15/4/28.
 */
public class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = "BaseFragmentActivity";
    private final int REQUEST_CODE_LOGIN = 101;

    private BaseFragmentActivity mBaseActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseActivity = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstant.ACTION_UPDATE_BADGE);
        registerReceiver(mReceiver,intentFilter);
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
          //TODO 需要跳转到新的登录界面
//        Intent intent = new Intent(this, LoginActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOGIN: {
                if (resultCode == RESULT_OK) {
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

    /**
     * 接受更新badge广播的接收器
     */
    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent){
                BadgeBean badgeBean = (BadgeBean) intent.getSerializableExtra("badgeBean");
                if(null != badgeBean){
                    if(mBaseActivity.getClass().getSimpleName().equals(badgeBean.getTargetActivity())){
                        int id = badgeBean.getTargetViewId();
                        View view = findViewById(id);
                        view.setVisibility(badgeBean.isSetVisiable() ? View.VISIBLE : View.INVISIBLE);
                        if(badgeBean.getType() == BadgeBean.Type.TEXT){
                            TextView tv = (TextView)view;
                            int former;
                            try {
                                former = Integer.parseInt(tv.getText().toString());
                            }catch (NumberFormatException e){
                                Log.e(TAG, "The number in badge has a invalid format.");
                                former = 0;
                            }
                            former += badgeBean.getCount();
                            if(former <= 0){
                                tv.setVisibility(View.INVISIBLE);
                                tv.setText("100+");
                            }else{
                                tv.setText(String.valueOf(former));
                            }

                        }
                    }
                }
            }
        }
    };
}
