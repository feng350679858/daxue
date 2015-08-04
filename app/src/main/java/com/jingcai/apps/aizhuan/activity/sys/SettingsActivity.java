package com.jingcai.apps.aizhuan.activity.sys;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.activity.index.MainActivity;
import com.jingcai.apps.aizhuan.activity.mine.MineSuggestionActivity;
import com.jingcai.apps.aizhuan.jpush.JpushUtil;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.stu.stu05.Stu05Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu05.Stu05Response;
import com.jingcai.apps.aizhuan.service.business.sys.sys05.Sys05Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.HXHelper;
import com.jingcai.apps.aizhuan.util.PopupWin;
import com.jingcai.apps.aizhuan.util.UmengShareUtil;
import com.jingcai.apps.aizhuan.util.VersionUtil;
import com.jingcai.apps.aizhuan.view.OnToggleStateChangeListener;
import com.jingcai.apps.aizhuan.view.SlideButton;

/**
 * Created by xiangqili on 2015/7/14.
 */
public class SettingsActivity extends BaseActivity {

    private AzService azService;
    private ProgressDialog progressDialog = null;
    private MessageHandler messageHandler;
    private LayoutInflater mInflater;
    private UmengShareUtil umengShareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_settings);
        azService = new AzService(this);
        messageHandler = new MessageHandler(this);
        initHeader();
        initView();
    }

    private void initHeader() {
        ((TextView) findViewById(R.id.tv_content)).setText("设置");
        findViewById(R.id.iv_func).setVisibility(View.GONE);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });
    }

    private void initView() {
        initVisiableSwitch();
        initVersionUpdate();
        initSuggestion();
        initModifyPsw();
        initAboutUs();
        initLogout();
        initInvite();

        initShareConfig();
    }

    private void initShareConfig() {
        umengShareUtil = new UmengShareUtil(SettingsActivity.this);
        umengShareUtil.setShareContent("爱赚万岁", "兼职必备神器，你难道还没用？", "http://www.izhuan365.com");
    }

    private void initInvite() {
        findViewById(R.id.ll_sys_setting_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengShareUtil.openShare();
            }
        });
    }

    private void initLogout() {
        findViewById(R.id.btn_sys_setting_logout).setOnClickListener(new View.OnClickListener() {
            PopupWin dialog;
            @Override
            public void onClick(View v) {
                if(mInflater == null){
                    mInflater = LayoutInflater.from(SettingsActivity.this);
                }
                View contentView = mInflater.inflate(R.layout.sys_comfirm_logout_dialog,null);
                View decorView = SettingsActivity.this.getWindow().getDecorView();
                if(dialog == null) {
                    dialog = PopupWin.Builder.create(SettingsActivity.this)
                            .setParentView(decorView)
                            .setContentView(contentView)
                            .build();
                }
                dialog.show();
                contentView.findViewById(R.id.btn_sys_logout_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        logout();
                    }
                });

                contentView.findViewById(R.id.btn_sys_logout_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void logout() {
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final Sys05Request req = new Sys05Request();
                Sys05Request.Student student = req.new Student();
                student.setStudentid(UserSubject.getStudentid());
                req.setStudent(student);
                azService.doTrans(req, Stu05Response.class, new AzService.Callback<Stu05Response>() {
                    @Override
                    public void success(Stu05Response resp) {
                        ResponseResult result = resp.getResult();
                        if (!"0".equals(result.getCode())) {
                            messageHandler.postMessage(3, result.getMessage());
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
    }

    private void initAboutUs() {
        findViewById(R.id.ll_sys_setting_about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initModifyPsw() {
        findViewById(R.id.ll_sys_setting_modify_psw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Intent intent = new Intent(SettingsActivity.this,ModifyPswActivity.class);
                 startActivity(intent);
            }
        });
    }

    private void initSuggestion() {
        findViewById(R.id.ll_sys_setting_suggestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MineSuggestionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initVersionUpdate() {
        findViewById(R.id.ll_sys_setting_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VersionUtil(SettingsActivity.this).autoUpdateApp(true);
            }
        });
    }

    private void initVisiableSwitch() {
        SlideButton slideButton = (SlideButton) findViewById(R.id.switch_setting_isvisiable);
        slideButton.setToggleState("1".equals(UserSubject.getIsvisiable()));
        slideButton.setOnToggleStateChangeListener(new OnToggleStateChangeListener() {
            @Override
            public void onToggleStateChange(final boolean isChecked) {
                new AzExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        final Stu05Request req = new Stu05Request();
                        Stu05Request.Student student = req.new Student();
                        student.setStudentid(UserSubject.getStudentid());
                        student.setIsvisiable(isChecked ? "1" : "0");
                        req.setStudent(student);
                        azService.doTrans(req, Stu05Response.class, new AzService.Callback<Stu05Response>() {
                            @Override
                            public void success(Stu05Response resp) {
                                ResponseResult result = resp.getResult();
                                if (!"0".equals(result.getCode())) {
                                    messageHandler.postMessage(1, result.getMessage());
                                } else {
                                    messageHandler.postMessage(0, isChecked);
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

    class MessageHandler extends BaseHandler {
        public MessageHandler(Context context) {
            super(context);
        }

        // closeProcessDialog();
        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    boolean isVisiable = (boolean) msg.obj;
                    StringBuffer sb = new StringBuffer("校友");
                    sb.append(isVisiable ? "能" : "不能");
                    sb.append("看见你的报名情况");
                    showToast(sb.toString());
                    break;
                }
                case 1: {
                    showToast("设置失败:" + msg.obj);
                    break;
                }
                case 2: {
                    UserSubject.loginFail();
                    new JpushUtil(SettingsActivity.this).logout();
                    HXHelper.getInstance().logout();  //环信连接

                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    SettingsActivity.this.finish();
                    break;
                }
                case 3: {
                    showToast("xczxczx" + msg.obj);
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    public void closeProcessDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }
}
