package com.jingcai.apps.aizhuan.activity.sys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.io.File;
import java.util.concurrent.CountDownLatch;

public class WelcomeActivity extends BaseActivity implements AnimationListener {
    private MessageHandler messageHandler;
    private AzExecutor azExecutor = new AzExecutor();
    private AzService azService = new AzService();
    private boolean loginSuccessFlag = false;
    private CountDownLatch latch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.sys_welcome);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);

        boolean hasLoadingImg = false;
        String loadingImgUrl = Preferences.getInstance().getString(Preferences.Sys.WELCOME_LOADING_IMG_URL, null);
        if (null != loadingImgUrl) {
            File file = BitmapUtil.getImageFile(loadingImgUrl);
            if (file.exists()) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                hasLoadingImg = true;
            }
        }
        if (!hasLoadingImg) {
            imageView.setImageResource(R.drawable.sys_welcome);
        }
        setContentView(imageView);

        messageHandler = new MessageHandler(this);
        if (GlobalConstant.debugFlag) {
            latch = new CountDownLatch(1);
        } else {
            latch = new CountDownLatch(2);
            Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.sys_welcome_alpha);
            alphaAnimation.setFillEnabled(true); // 启动Fill保持
            alphaAnimation.setFillAfter(true); // 设置动画的最后一帧是保持在View上面
            alphaAnimation.setAnimationListener(this); // 为动画设置监听

//            LinearLayout imageView2 = (LinearLayout) findViewById(R.id.welcome_image_view);
            imageView.setAnimation(alphaAnimation);
        }
        autoLogin();
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        latch.countDown();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
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
