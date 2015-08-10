package com.jingcai.apps.aizhuan.activity.sys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu04.Stu04Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys01.Sys01Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys02.Sys02Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys02.Sys02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.view.ClearableEditText;

/**
 * Created by Json Ding on 2015/4/28.
 */
public class RegistActivity extends BaseActivity {
    private AzExecutor azExecutor;
    private AzService azService;
    private MessageHandler messageHandler;
    private ClearableEditText et_phone, et_checkstr, et_password, et_repeat_password;
    private TextView btn_send_checkstr;

    private boolean forgetFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_regist);

        forgetFlag = getIntent().getBooleanExtra("forgetFlag", false);

        messageHandler = new MessageHandler(this);
        azExecutor = new AzExecutor();
        azService = new AzService(this);
        initViews();
    }

    private void initViews() {
        et_phone = (ClearableEditText) findViewById(R.id.et_phone);
        et_checkstr = (ClearableEditText) findViewById(R.id.et_checkstr);
        et_password = (ClearableEditText) findViewById(R.id.et_password);
        et_repeat_password = (ClearableEditText) findViewById(R.id.et_repeat_password);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageHandler.postMessage(9);
            }
        });
        btn_send_checkstr = (TextView) findViewById(R.id.btn_send_checkstr);
        btn_send_checkstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (11 != et_phone.getText().toString().length()) {
                    showToast("请输入11位手机号", 0);
                    return;
                }
                azExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Sys02Request req = new Sys02Request();
                        Sys02Request.Student stu = req.new Student();
                        stu.setPhone(et_phone.getText().toString());
                        req.setStudent(stu);
                        azService.doTrans(req, Sys02Response.class, new AzService.Callback<Sys02Response>() {
                            @Override
                            public void success(Sys02Response response) {
                                ResponseResult result = response.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    messageHandler.postMessage(2);
                                }
                            }

                            @Override
                            public void fail(AzException e) {
                                messageHandler.postException(e);
                            }
                        });
                    }
                });
                azExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 59; i >= 0; i--) {
                                messageHandler.postMessage(5, i);
                                Thread.sleep(1000L);
                            }
                        } catch (InterruptedException e) {
                            messageHandler.postMessage(5, 0);
                        }
                    }
                });
            }
        });


        if (forgetFlag) {
            findViewById(R.id.civ_head_logo).setVisibility(View.GONE);
            findViewById(R.id.layout_regist_confirm).setVisibility(View.GONE);
            findViewById(R.id.tv_has_account).setVisibility(View.GONE);
            findViewById(R.id.btn_regist).setVisibility(View.GONE);

            findViewById(R.id.tv_forget_pwd_tip).setVisibility(View.VISIBLE);

            Button btn_forget_pwd = (Button) findViewById(R.id.btn_forget_pwd);
            btn_forget_pwd.setVisibility(View.VISIBLE);
            btn_forget_pwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    azExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Stu04Request req = new Stu04Request();
                            Stu04Request.Student stu = req.new Student();
                            stu.setPhone(et_phone.getText().toString());
                            stu.setCheckstr(et_checkstr.getText().toString());
                            String encryptPassword = DES3Util.encrypt(et_password.getText().toString());
                            stu.setPassword(encryptPassword);
                            req.setStudent(stu);
                            azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                @Override
                                public void success(BaseResponse response) {
                                    ResponseResult result = response.getResult();
                                    if (!"0".equals(result.getCode())) {
                                        messageHandler.postMessage(7, result.getMessage());
                                    } else {
                                        messageHandler.postMessage(8);
                                    }
                                }

                                @Override
                                public void fail(AzException e) {
                                    messageHandler.postException(e);
                                }
                            });
                        }
                    });
                }
            });
        } else {
            //服务条款
            findViewById(R.id.tv_service_rule).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 显示服务条款
                }
            });
            Button btn_regist = (Button) findViewById(R.id.btn_regist);
            btn_regist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkRegist()) {
                        return;
                    }
                    azExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Sys01Request req = new Sys01Request();
                            Sys01Request.Student stu = req.new Student();
                            stu.setPhone(et_phone.getText().toString());
                            stu.setCheckstr(et_checkstr.getText().toString());
                            String encryptPassword = DES3Util.encrypt(et_password.getText().toString());
                            stu.setPassword(encryptPassword);
                            req.setStudent(stu);
                            azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                                @Override
                                public void success(BaseResponse response) {
                                    ResponseResult result = response.getResult();
                                    if (!"0".equals(result.getCode())) {
                                        messageHandler.postMessage(3, result.getMessage());
                                    } else {
                                        messageHandler.postMessage(4);
                                    }
                                }

                                @Override
                                public void fail(AzException e) {
                                    messageHandler.postException(e);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private boolean checkRegist() {
        if (11 != et_phone.getText().toString().length()) {
            showToast("请输入11位手机号", 0);
            return false;
        }
        int length = et_password.getText().toString().length();
        if (6 > length || length > 16) {
            showToast("密码长度必须在6至16位之间", 0);
            return false;
        }
        if (!et_password.getText().toString().equals(et_repeat_password.getText().toString())) {
            showToast("密码输入不一致", 0);
            return false;
        }
        return true;
    }

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    if (null != msg.obj) {
                        showToast(String.valueOf(msg.obj), 0);
                    } else {
                        showToast("发送验证码失败", 0);
                    }
                    break;
                }
                case 2: {
                    showToast("发送验证码成功", 0);
                    break;
                }
                case 3: {
                    if (null != msg.obj) {
                        showToast(String.valueOf(msg.obj), 0);
                    } else {
                        showToast("注册失败", 0);
                    }
                    break;
                }
                case 4: {
                    showToast("注册成功", 0);
                    Intent intent = new Intent();
                    intent.putExtra("phone", et_phone.getText().toString());
                    intent.putExtra("password", et_password.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }
                case 5: {
                    int count = (int) msg.obj;
                    if (count > 0) {
                        btn_send_checkstr.setText(count + "秒后重发");
                        if (btn_send_checkstr.isEnabled()) {
                            btn_send_checkstr.setEnabled(false);
                        }
                    } else {
                        btn_send_checkstr.setText("发送验证");
                        if (!btn_send_checkstr.isEnabled()) {
                            btn_send_checkstr.setEnabled(true);
                        }
                    }
                    break;
                }
                case 7: {
                    if (null != msg.obj) {
                        showToast(String.valueOf(msg.obj), 0);
                    } else {
                        showToast("重置密码失败", 0);
                    }
                    break;
                }
                case 8: {
                    showToast("重置密码成功", 0);
                    Intent intent = new Intent();
                    intent.putExtra("phone", et_phone.getText().toString());
                    intent.putExtra("password", et_password.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }
                case 9: {
                    setResult(RESULT_CANCELED, null);
                    finish();
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        setResult(RESULT_CANCELED, null);
//        finish();
//       // super.onBackPressed();
//    }
}
