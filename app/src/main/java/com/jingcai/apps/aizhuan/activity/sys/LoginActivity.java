package com.jingcai.apps.aizhuan.activity.sys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.jpush.JpushUtil;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Request;
import com.jingcai.apps.aizhuan.service.business.sys.sys04.Sys04Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.HXHelper;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/4/27.
 */
public class LoginActivity extends BaseActivity {
    private final int REQUEST_CODE_REGIST = 101;
    private final int REQUEST_CODE_FORGET_PWD = 102;
    private EditText et_username, et_password;
    private MessageHandler messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_login);

        messageHandler = new MessageHandler(this);

        if(UserSubject.isLogin()){
            doLogin(UserSubject.getPhone(), DES3Util.decrypt(UserSubject.getPassword()));
        }else {
            initViews();
        }
    }

    private void initViews() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        if(StringUtil.isNotEmpty(UserSubject.getPhone())){
            et_username.setText(UserSubject.getPhone());
        }

        findViewById(R.id.btn_login).setOnClickListener(loginListener);
        findViewById(R.id.btn_fegret_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                intent.putExtra("forgetFlag", true);
                startActivityForResult(intent, REQUEST_CODE_FORGET_PWD);
//                finish();
            }
        });
        findViewById(R.id.btn_regist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivityForResult(intent, REQUEST_CODE_REGIST);
                //finish();
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageHandler.postMessage(4);
            }
        });
    }

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();
            if (username.length() < 1 || password.length() < 1) {
                //showDialog(WSUtil.DIALOG_USER_PWD_EMPTY);
                showToast("账号密码不能为空");
                return;
            }
            showProgressDialog("正在登录...");
            doLogin(username, password);
        }
    };

    private void doLogin(final String phone, final String password) {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Sys04Request req = new Sys04Request();
                Sys04Request.Student stu = req.new Student();
                stu.setPhone(phone);
                final String encryptPassword = DES3Util.encrypt(password);
                stu.setPassword(encryptPassword);
//                Log.d("==", encryptPassword);
//                Log.d("==", DES3Util.decrypt(encryptPassword));
//                System.out.println("--:" + encryptPassword);
//                System.out.println("--:"+DES3Util.decrypt(encryptPassword));
                req.setStudent(stu);

                final AzService azService = new AzService(LoginActivity.this);
                azService.doTrans(req, Sys04Response.class, new AzService.Callback<Sys04Response>() {
                    @Override
                    public void success(Sys04Response response) {
                        ResponseResult result = response.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(1, result.getMessage());
                        } else {
                            Sys04Response.Sys04Body sys04Body = response.getBody();
                            getStudentInfo(azService, sys04Body.getStudent().getStudentid(), encryptPassword);
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



    private void getStudentInfo(AzService azService, final String studentid, final String encryptPassword) {
        Stu02Request req = new Stu02Request();
        final Stu02Request.Student stu = req.new Student();
        stu.setStudentid(studentid);
        req.setStudent(stu);
        azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
            @Override
            public void success(Stu02Response response) {
                ResponseResult result = response.getResult();
                Stu02Response.Stu02Body stu02Body = response.getBody();
                Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                student.setStudentid(studentid);
                student.setPassword(encryptPassword);
                if(!"0".equals(result.getCode())) {
                    messageHandler.postMessage(2, result.getMessage());
                }else{
                    messageHandler.postMessage(0, student);
                }
            }
            @Override
            public void fail(AzException e) {
                messageHandler.postException(e);
            }
        });
    }

    class MessageHandler extends BaseHandler {
        MessageHandler(Context ctx) {
            super(ctx);
        }
        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0:{
                    showToast("登录成功");
                    Stu02Response.Stu02Body.Student student = (Stu02Response.Stu02Body.Student) msg.obj;
                    UserSubject.loginSuccess(student);
                    new JpushUtil(LoginActivity.this).login(student.getStudentid());
                    HXHelper.getInstance().loginOnEMChatServer(student.getStudentid());  //环信连接
                    setResult(RESULT_OK);
                    finish();
                    break;
                }
                case 1:{
                    showToast("登录失败,"+ msg.obj);
                    UserSubject.loginFail();
                    break;
                }
                case 3:{
                    showToast("获取用户信息失败");
                    break;
                }
                case 4:{
                    showToast("已取消登录");
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
                }
                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_REGIST: {
                /**
                 * 注册成功，则直接登录
                 * 否则，停留在登录页面
                 */
                if (resultCode == Activity.RESULT_OK) {
                    String phone = data.getStringExtra("phone");
                    String password = data.getStringExtra("password");
                    doLogin(phone, password);
                }
                break;
            }
            /**
             * 重置密码成功，填写登录手机号，需要重新登录
             * 否则，停留在登录页面
             */
            case REQUEST_CODE_FORGET_PWD: {
                if (resultCode == Activity.RESULT_OK) {
                    String phone = data.getStringExtra("phone");
                    et_username.setText(phone);
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
