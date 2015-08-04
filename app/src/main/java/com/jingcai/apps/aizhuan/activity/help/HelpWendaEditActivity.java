package com.jingcai.apps.aizhuan.activity.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
import com.jingcai.apps.aizhuan.service.base.ResponseResult;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob14.Partjob14Request;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob14.Partjob14Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * Created by lejing on 15/7/16.
 */
public class HelpWendaEditActivity extends BaseActivity {
    private static final String TAG = "HelpWendaCommnet";
    private String helpid;
    private MessageHandler messageHandler;
    private TextView tv_func;
    private TextView et_help_content;
    private CheckBox cb_anonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helpid = getIntent().getStringExtra("answerid");
        if(StringUtil.isEmpty(helpid)){
            finish();
        } else {
            messageHandler = new MessageHandler(this);
            setContentView(R.layout.help_wenda_edit);

            initHeader();

            initView();
        }
    }


    private void initHeader() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        tvTitle.setVisibility(View.GONE);

        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer();
            }
        });


        tv_func = (TextView) findViewById(R.id.tv_func);
        tv_func.setVisibility(View.VISIBLE);
        tv_func.setText("发布");
        tv_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                        Intent intent = new Intent();
                        intent.putExtra("answerid", String.valueOf(msg.obj));
                        setResult(RESULT_OK, intent);
                        finish();
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
                default: {
                    super.handleMessage(msg);
                }
            }
        }
    }
    private void answer() {
        if(!actionLock.tryLock()){
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob14Request req = new Partjob14Request();
                Partjob14Request.Parttimejob job = req.new Parttimejob();
                job.setAnonflag(cb_anonymous.isChecked()?"1":"0");
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
    }
}
