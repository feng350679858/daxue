package com.jingcai.apps.aizhuan.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jingcai.apps.aizhuan.R;
import com.jingcai.apps.aizhuan.activity.base.BaseActivity;
import com.jingcai.apps.aizhuan.activity.common.BaseHandler;
import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.base.BaseResponse;
import com.jingcai.apps.aizhuan.service.business.partjob.partjob12.Partjob12Request;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.StringUtil;

/**
 * This Activity required
 * INTENT_NAME_STUDENT_ID ,
 * INTENT_NAME_STUDENT_NAME,
 * INTENT_NAME_TARGET_ID
 * in String extra,If empty,finish() automatic.
 * Created by Json Ding on 2015/7/31.
 */
public class CommentReplyActivity extends BaseActivity {

    public static final String INTENT_NAME_STUDENT_ID = "studentId";
    public static final String INTENT_NAME_STUDENT_NAME = "studentName";
    public static final String INTENT_NAME_TARGET_ID = "targetId";
    private static final String TAG = "CommentReplyActivity";

    private EditText mEtContent;
    private String mStudentId;
    private String mStudentName;
    private String mTargetId;

    private AzService azService;
    private MessageHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_comment_reply);
        mHandler = new MessageHandler(this);
        getIntentExtra();
        initHeader();
        initView();
    }

    private void getIntentExtra() {
        final Intent intent = getIntent();
        if (intent == null) {
            Log.d(TAG, "CommentReplyActivity required a intent with data");
            finish();
            return;
        }
        mStudentId = intent.getStringExtra(INTENT_NAME_STUDENT_ID);
        mStudentName = intent.getStringExtra(INTENT_NAME_STUDENT_NAME);
        mTargetId = intent.getStringExtra(INTENT_NAME_TARGET_ID);
        if(StringUtil.isEmpty(mStudentId)
                || StringUtil.isEmpty(mStudentName)
                || StringUtil.isEmpty(mTargetId)){
            Log.d(TAG, "CommentReplyActivity required NOT EMPTY String extra");
            finish();
        }

    }

    private void initView() {
        mEtContent = (EditText) findViewById(R.id.et_reply_content);
        mEtContent.setHint("回复" + mStudentName + "：");
    }

    private void initHeader() {
        ImageButton btnBack = (ImageButton) findViewById(R.id.ib_back);
        TextView tvTitle = (TextView) findViewById(R.id.tv_content);
        findViewById(R.id.rl_iv_func_container).setVisibility(View.GONE);
        TextView tvFunc = (TextView) findViewById(R.id.tv_func);
        tvFunc.setVisibility(View.VISIBLE);
        tvFunc.setText("发送");
        tvFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReply();
            }
        });

        tvTitle.setText("回复评论");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendReply() {
        final String content = mEtContent.getText().toString();
        if(!checkInput(content)){
            return;
        }
        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Partjob12Request req = new Partjob12Request();
                Partjob12Request.Parttimejob parttimejob = req.new Parttimejob();
                parttimejob.setSourceid(UserSubject.getStudentid());
                parttimejob.setTargettype("4");  //评论本身
                parttimejob.setTargetid(mTargetId);
                parttimejob.setContent(content);
                parttimejob.setOptype("1");  //评论
                req.setParttimejob(parttimejob);

                if (null == azService) {
                    azService = new AzService(CommentReplyActivity.this);
                }

                azService.doTrans(req, BaseResponse.class, new AzService.Callback<BaseResponse>() {
                    @Override
                    public void success(BaseResponse resp) {
                        String resultCode = resp.getResultCode();
                        if("0".equals(resultCode)){
                            mHandler.sendEmptyMessage(0);
                        }else{
                            mHandler.postMessage(1,resp.getResultMessage());
                        }

                    }

                    @Override
                    public void fail(AzException e) {

                    }
                });
            }
        });

    }

    private boolean checkInput(String content) {
        boolean flag = true;
        String tip = null;
        if(content.length() < 5){
            tip = "至少要5个字哦";
            flag = false;
        }

        if(!flag){
            showToast(tip);
        }
        return flag;
    }

    private class MessageHandler extends BaseHandler{

        public MessageHandler(Context ctx) {
            super(ctx);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showToast("回复成功");
                    finish();
                    break;
                case 1:
                    showToast("回复失败，请稍后重试");
                    Log.e(TAG,"Comment reply failed:"+msg.obj.toString());
                    break;
                default: super.handleMessage(msg);

            }
        }
    }
}
