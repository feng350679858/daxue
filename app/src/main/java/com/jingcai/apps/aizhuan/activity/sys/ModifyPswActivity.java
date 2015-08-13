package com.jingcai.apps.aizhuan.activity.sys;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu01.Stu01Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu01.Stu01Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.DES3Util;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by Json Ding on 2015/5/5.
 */
public class ModifyPswActivity extends BaseActivity {
    private MessageHandler messageHandler;


    private EditText mTxtOldPassword;
    private EditText mTxtNewPassword;
    private EditText mTxtNewPassword2;
    private Button mBtnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sys_modify_psw);
            messageHandler = new MessageHandler(this);
        initHeader();
        initView();  //初始化控件
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("修改登录密码");
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTxtOldPassword = (EditText) findViewById(R.id.et_sys_modify_psw_old_psw);
        mTxtNewPassword = (EditText) findViewById(R.id.et_sys_modify_psw_new_psw);
        mTxtNewPassword2 = (EditText) findViewById(R.id.et_sys_modify_psw_new_psw_2);

        mTxtOldPassword.addTextChangedListener(mSubmitButtonMonitor);
        mTxtNewPassword.addTextChangedListener(mSubmitButtonMonitor);
        mTxtNewPassword2.addTextChangedListener(mSubmitButtonMonitor);

        mBtnSubmit = (Button) findViewById(R.id.btn_sys_modify_psw_submit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AzExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(StringUtil.isEmpty(mTxtOldPassword.getText().toString())) {
                            messageHandler.postMessage(3, "请输入旧密码");
                            return ;
                        }
                        if(StringUtil.isEmpty(mTxtNewPassword.getText().toString())){
                            messageHandler.postMessage(3, "请输入新密码");
                            return ;
                        }
                        if(mTxtNewPassword.getText().toString().length()<6){
                            messageHandler.postMessage(3, "新密码至少6位长度");
                            return ;
                        }
                        if(!mTxtNewPassword.getText().toString().equals(mTxtNewPassword2.getText().toString())){
                            messageHandler.postMessage(2);  //密码不一致
                            return ;
                        }
                        final AzService azService = new AzService(ModifyPswActivity.this);
                        Stu01Request req = new Stu01Request();
                        Stu01Request.Student student = req.new Student();

                        student.setStudentid(UserSubject.getStudentid());
                        student.setNewpassword(DES3Util.encrypt(mTxtNewPassword.getText().toString()));
                        student.setOldpassword(DES3Util.encrypt(mTxtOldPassword.getText().toString()));
                        req.setStudent(student);
                        azService.doTrans(req, Stu01Response.class,new AzService.Callback<Stu01Response>() {
                            @Override
                            public void success(Stu01Response resp) {
                                ResponseResult result = resp.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    messageHandler.postMessage(0);
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

    private TextWatcher mSubmitButtonMonitor = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String oldPassword = mTxtOldPassword.getText().toString();
            String newPassword = mTxtNewPassword.getText().toString();
            String newPassword2 = mTxtNewPassword2.getText().toString();

            if(StringUtil.isEmpty(oldPassword)
                    || StringUtil.isEmpty(newPassword)
                    || StringUtil.isEmpty(newPassword2)){
                mBtnSubmit.setEnabled(false);
                mBtnSubmit.setTextColor(getResources().getColor(R.color.assist_grey));
            }else{
                mBtnSubmit.setEnabled(true);
                mBtnSubmit.setTextColor(getResources().getColor(R.color.important_dark));
            }
        }
    };


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx){
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what){
                case 0:{
                    showToast("密码修改成功");
                    finish();
                    break;
                }
                case 1:{
                    showToast("密码修改失败："+ msg.obj);
                    break;
                }
                case 2:{
                    showToast("新密码输入不一致");
                    break;
                }
                case 3:{
                    showToast(String.valueOf(msg.obj));
                    break;
                }

                default:{
                    super.handleMessage(msg);
                }
            }
        }
    }
}
