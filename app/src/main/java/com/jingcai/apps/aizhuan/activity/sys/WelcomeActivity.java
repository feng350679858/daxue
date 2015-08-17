package com.jingcai.apps.aizhuan.activity.sys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.persistence.GlobalConstant;
import com.jingcai.apps.aizhuan.persistence.Preferences;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.BitmapUtil;

import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;

public class WelcomeActivity extends BaseActivity { //implements AnimationListener {
    private MessageHandler messageHandler;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private boolean loginSuccessFlag = false;
    private CountDownLatch latch;
    private ImageView mIvWelcomeText;
    private LinearLayout mLlLogoContainer;
    private CircleImageView mCivHeadImg;
    private TextView mTvSchoolName;

    private BitmapUtil mBitmapUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_welcome);
        mBitmapUtil = new BitmapUtil(this);

        messageHandler = new MessageHandler(this);
        initViews();
        if (GlobalConstant.debugFlag) {
            latch = new CountDownLatch(1);
        } else {
            latch = new CountDownLatch(2);
            playAnimator();
        }
        autoLogin();
    }

    private void initViews() {
        mIvWelcomeText = (ImageView) findViewById(R.id.iv_welcome_text);
        mLlLogoContainer = (LinearLayout) findViewById(R.id.ll_logo_container);
        mCivHeadImg = (CircleImageView) findViewById(R.id.civ_head_img);
        mTvSchoolName = (TextView) findViewById(R.id.tv_school_name);

        if (UserSubject.isLogin()) {
            mBitmapUtil.getImage(mCivHeadImg, UserSubject.getLogourl());
        } else {
           mCivHeadImg.setImageResource(R.drawable.default_head_img);
            mTvSchoolName.setText("欢迎回来");
        }
    }

    private void playAnimator() {

        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(mIvWelcomeText, "alpha", 1.0f, 0f).setDuration(1000);
        ObjectAnimator fadeInAnimator1 = ObjectAnimator.ofFloat(mCivHeadImg, "alpha", 0f, 1.0f).setDuration(1000);
        ValueAnimator fadeInAnimator2 = ValueAnimator.ofObject(new ArgbEvaluator(), 0x00222222, 0xff222222).setDuration(1000);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mCivHeadImg, "scaleX", 0.3f, 1.0f).setDuration(2000);
        scaleXAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mCivHeadImg, "scaleY", 0.3f, 1.0f).setDuration(2000);
        scaleYAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(mTvSchoolName, "translationY", 100.0f, 0f).setDuration(2000);
        transAnimator.setInterpolator(new OvershootInterpolator());
        fadeInAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mTvSchoolName.setTextColor((Integer) animator.getAnimatedValue());
            }

        });

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(fadeInAnimator1,fadeInAnimator2,scaleXAnimator,scaleYAnimator,transAnimator);

        fadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIvWelcomeText.setVisibility(View.GONE);
                mLlLogoContainer.setVisibility(View.VISIBLE);
                animSet.start();
            }
        });


        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                latch.countDown();
            }
        });

        fadeOutAnimator.setStartDelay(1000);
        fadeOutAnimator.start();


    }

    private void autoLogin() {
        azExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (UserSubject.isLogin()) {
                    azExecutor.execute(loginCommand);
                } else {
                    loginSuccessFlag = false;
                    latch.countDown();
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                }
                messageHandler.postMessage(3);
            }
        });
    }

    private Runnable loginCommand = new Runnable() {
        @Override
        public void run() {
            Sys04Request req = new Sys04Request();
            Sys04Request.Student stu = req.new Student();
            stu.setPhone(UserSubject.getPhone());
            String encryptPassword = UserSubject.getPassword();
            stu.setPassword(encryptPassword);
            req.setStudent(stu);
            azService.doTrans(req, Sys04Response.class, new AzService.Callback<Sys04Response>() {
                @Override
                public void success(Sys04Response response) {
                    ResponseResult result = response.getResult();
                    if ("0".equals(result.getCode())) {
                        messageHandler.postMessage(1);
                    } else {
                        messageHandler.postMessage(2);
                    }
                }

                @Override
                public void fail(AzException e) {
                    try {
                        messageHandler.postException(e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
    };

    private final int REQUEST_CODE_INTRO = 101;

    private class MessageHandler extends BaseHandler {
        public MessageHandler(Context con) {
            super(con);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {//登录成功
                    loginSuccessFlag = true;
                    latch.countDown();
                    break;
                }
                case 2: {//登录失败
                    loginSuccessFlag = false;
                    UserSubject.loginFail();
                    latch.countDown();
                    break;
                }
                case 3: {
                    if ("1".equals(Preferences.getInstance().getString(Preferences.Sys.PARAM_INTROSTATUS))) {
                        if (loginSuccessFlag) {
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            startActivityForLogin();
                        }
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, IntroActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_INTRO);
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_INTRO: {
                if (resultCode == Activity.RESULT_OK) {
                    Preferences.getInstance().update(Preferences.Sys.PARAM_INTROSTATUS, "1");
                    messageHandler.postMessage(3);
                    break;
                } else {
                    finish();
                    break;
                }
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void afterLoginSuccess() {
        loginSuccessFlag = true;
        messageHandler.postMessage(3);
    }

    @Override
    protected void afterLoginFail() {
        finish();
    }
}
