package com.jingcai.apps.aizhuan.activity.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.widget.TopToast2;

public class BaseHandler extends Handler {
    private final String TAG = "==" + BaseHandler.class.getName();
    private Context context;
    private int title_height;

    public BaseHandler(Context ctx) {
        super(Looper.myLooper());
        this.context = ctx;
        title_height = context.getResources().getDimensionPixelSize(R.dimen.header_height);
    }

    public BaseHandler(Context ctx, Looper looper) {
        super(looper);
        this.context = ctx;
    }

    public void postMessage(int what) {
        obtainMessage(what, null).sendToTarget();
    }

    public void postMessage(int what, Object obj) {
        obtainMessage(what, obj).sendToTarget();
    }

    public void postException(AzException e) {
        obtainMessage(e.getCode(), e.getMessage()).sendToTarget();
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1000: {//通用提示
                if (msg.obj != null) {
                    showInnerToast(String.valueOf(msg.obj));
                } else {
                    showInnerToast("网络连接失败！");
                }
                break;
            }
            case 1001: {//解析返回异常
                Log.e(TAG, String.valueOf(msg.obj));
                showInnerToast("解析服务端返回异常");
                break;
            }
            case 1002: {//未登录异常
                Log.e(TAG, String.valueOf(msg.obj));
                if (UserSubject.isLogin()) {
                    relogin();//重新尝试登录系统
                } else {
                    showInnerToast("请重新登录");
                    UserSubject.loginFail();
                }
                break;
            }
            case 1003: {//超时
                Log.e(TAG, String.valueOf(msg.obj));
                showInnerToast("请求超时，请重试");
                break;
            }
            case 1004: {//已重新登录
                Log.e(TAG, String.valueOf(msg.obj));
                showInnerToast("会话过期，已重新登录，请重试");
                break;
            }
        }
    }

    private void relogin() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Sys04Request req = new Sys04Request();
                Sys04Request.Student stu = req.new Student();
                stu.setPhone(UserSubject.getPhone());
                final String encryptPassword = UserSubject.getPassword();
                stu.setPassword(encryptPassword);
                req.setStudent(stu);
                final AzService azService = new AzService(context);
                azService.doTrans(req, Sys04Response.class, new AzService.Callback<Sys04Response>() {
                    @Override
                    public void success(Sys04Response response) {
                        ResponseResult result = response.getResult();
                        if ("0".equals(result.getCode())) {
                            //服务端登录成功
                            BaseHandler.this.postMessage(1004);
                        } else {
                            UserSubject.loginFail();
                            //登录失败
                            BaseHandler.this.postMessage(1002);
                        }
                    }

                    @Override
                    public void fail(AzException e) {
                        BaseHandler.this.postException(e);
                    }
                });
            }
        });
    }

    protected void showInnerToast(String msg) {
        showInnerToast(msg, title_height);
    }

    protected void showInnerToast(String msg, int height) {
//        TopToast topToast = TopToast.makeText(context, msg, TopToast.LENGTH_LONG);
//        topToast.setOffsetY(title_height);
//        topToast.show();
        TopToast2.showToast(context, msg, height);
    }
}