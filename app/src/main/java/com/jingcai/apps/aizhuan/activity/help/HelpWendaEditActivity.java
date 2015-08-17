package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob14.Partjob14Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob14.Partjob14Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob36.Partjob36Response;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob39.Partjob39Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaEditActivity extends BaseActivity {
    private String helpid, answerid;
    private MessageHandler messageHandler;
    private TextView tv_func;
    private TextView et_help_content;
    private CheckBox cb_anonymous;
    private boolean updateFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("helpid");
        answerid = getIntent().getStringExtra("answerid");
        if (StringUtil.isEmpty(helpid)) {
            finishWithResult();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_edit);

            initHeader();

            initView();

            initData();
        }
    }

    private void finishWithResult() {
        if (updateFlag) {
            Intent intent = new Intent();
            intent.putExtra("answerid", answerid);
            intent.putExtra("helpContent", et_help_content.getText().toString());
            intent.putExtra("anonFlag", cb_anonymous.isChecked());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void initData() {
        if (StringUtil.isEmpty(answerid)) return;
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob36Request req = new Partjob36Request(answerid, UserSubject.getStudentid());
                new AzService().doTrans(req, Partjob36Response.class, new AzService.Callback<Partjob36Response>() {
                    @Override
                    public void success(Partjob36Response resp) {
                        if ("0".equals(resp.getResultCode())) {
                            Partjob36Response.Parttimejob job = resp.getBody().getParttimejob();
                            messageHandler.postMessage(2, job);
                        } else {
                            messageHandler.postMessage(3);
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


    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setVisibility(View.GONE);

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });


        findViewById(R.id.rl_iv_func_container).setVisibility(View.GONE);
        tv_func = (TextView) findViewById(R.id.tv_func);
        tv_func.setVisibility(View.VISIBLE);
        tv_func.setText("发布");
        tv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer();
            }
        });
    }

    private void initView() {
        et_help_content = (EditText) findViewById(R.id.et_help_content);
        cb_anonymous = (CheckBox) findViewById(R.id.cb_anonymous);
        showInputMethodDialog(et_help_content);
    }


    class MessageHandler extends BaseHandler {
        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            closeProcessDialog();
            switch (msg.what) {
                case 0: {
                    try {
                        showToast("发布成功");
                        answerid = String.valueOf(msg.obj);
                        updateFlag = true;
                        finishWithResult();
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 1: {
                    try {
                        showToast("发布失败：" + msg.obj.toString());
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 2: {
                    try {
                        Partjob36Response.Parttimejob job = (Partjob36Response.Parttimejob) msg.obj;
                        et_help_content.setText(job.getContent());
                        cb_anonymous.setChecked("1".equals(job.getAnonflag()));
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                case 3: {
                    try {
                        showToast("获取答案详情失败：" + msg.obj.toString());
                    } finally {
                        actionLock.unlock();
                    }
                    break;
                }
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }

    private void answer() {
        if (!actionLock.tryLock()) {
            return;
        }
        if(StringUtil.isEmpty(answerid)) {
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Partjob14Request req = new Partjob14Request();
                    Partjob14Request.Parttimejob job = req.new Parttimejob();
                    job.setAnonflag(cb_anonymous.isChecked() ? "1" : "0");
                    job.setContent(et_help_content.getText().toString());
                    job.setQuestionid(helpid);
                    job.setStudentid(UserSubject.getStudentid());
                    req.setParttimejob(job);
                    new AzService().doTrans(req, Partjob14Response.class, new AzService.Callback<Partjob14Response>() {
                        @Override
                        public void success(Partjob14Response resp) {
                            if ("0".equals(resp.getResultCode())) {
                                String answerid = resp.getBody().getParttimejob().getHelperid();
                                messageHandler.postMessage(0, answerid);
                            } else {
                                messageHandler.postMessage(1, resp.getResultMessage());
                            }
                        }

                        @Override
                        public void fail(AzException e) {
                            messageHandler.postException(e);
                        }
                    });
                }
            });
        }else{
            new AzExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Partjob39Request req = new Partjob39Request(UserSubject.getStudentid(), answerid, et_help_content.getText().toString());
                    req.getParttimejob().setAnonflag(cb_anonymous.isChecked() ? "1" : "0");
                    new AzService().doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                        @Override
                        public void success(BaseResponse resp) {
                            if ("0".equals(resp.getResultCode())) {
                                messageHandler.postMessage(0, answerid);
                            } else {
                                messageHandler.postMessage(1, resp.getResultMessage());
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishWithResult();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
